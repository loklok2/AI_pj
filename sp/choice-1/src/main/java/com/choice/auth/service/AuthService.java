package com.choice.auth.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.choice.auth.dto.LoginRequestDTO;
import com.choice.auth.dto.LoginResponseDTO;
import com.choice.auth.dto.SignupRequestDTO;
import com.choice.auth.entity.Member;
import com.choice.auth.entity.Role;
import com.choice.auth.repository.MemberRepository;
import com.choice.config.JWTUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 회원가입
    @Transactional
    public void registerUser(SignupRequestDTO requestDto) {
        // 사용자 이름 중복 검사
        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 사용자 이름입니다.");
        }

        // 이메일 중복 검사
        if (memberRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }

        // 주민등록번호에서 생년월일과 성별 추출
        String residentRegistrationNumber = requestDto.getResidentRegistrationNumber();
        if (residentRegistrationNumber.length() != 7) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "주민등록번호는 앞 7자리를 입력해야 합니다.");
        }
        char genderCode = residentRegistrationNumber.charAt(6);
        String gender = (genderCode == '1' || genderCode == '3') ? "남자" : "여자";

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 생성 및 저장
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .name(requestDto.getName())
                .residentRegistrationNumber(residentRegistrationNumber)
                .gender(gender)
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .email(requestDto.getEmail())
                // .nickname(requestDto.getNickname())
                .role(Role.MEMBER)
                .joinDate(LocalDateTime.now())
                .editedDate(LocalDateTime.now())
                .enabled(false)
                .style(requestDto.getStyle())
                .build();

        // 회원 정보 저장
        Member savedMember = memberRepository.save(member);

        // 이메일 인증 토큰 생성 및 저장
        String emailVerificationToken = JWTUtil.getEmailVerificationToken(savedMember.getUsername());
        emailService.sendVerificationEmail(member.getEmail(), emailVerificationToken); // 이메일 인증 메일 발송
    }

    // 로그인
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Member member = memberRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        if (!member.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이메일 인증이 완료되지 않았습니다.");
        }

        String accessToken = JWTUtil.getJWT(member.getUsername());
        String refreshToken = JWTUtil.getRefreshToken(member.getUsername());

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    // 이메일 인증
    @Transactional
    public boolean verifyEmail(String token) {
        if (JWTUtil.isExpired(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "만료된 토큰입니다.");
        }
        String username = JWTUtil.getClaim(token);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        member.setEnabled(true);
        memberRepository.save(member);
        return true;
    }

    // 24시간 이후에 인증 토큰이 만료되면 사용자 계정 삭제
    @Scheduled(fixedRate = 86400000) // 24시간마다 실행 (24 * 60 * 60 * 1000 ms)
    @Transactional
    public void deleteUnverifiedMembers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Member> expiredMembers = memberRepository.findByEnabledAndJoinDateBefore(false, cutoffTime);

        for (Member member : expiredMembers) {
            memberRepository.delete(member);
        }
    }

    // 아이디 찾기
    public String findUsername(String name, String email) {
        return memberRepository.findByNameAndEmail(name, email)
                .map(Member::getUsername) // 사용자 이름 반환
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 사용자를 찾을 수 없습니다."));
    }

    // 비밀번호 재설정 이메일 발송
    public void requestPasswordReset(String username, String email) {
        Member member = memberRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 사용자를 찾을 수 없습니다."));

        String resetToken = JWTUtil.getPasswordResetToken(member.getUsername());
        emailService.sendPasswordResetEmail(member.getEmail(), resetToken); // 비밀번호 재설정 메일 발송
    }

    // 비밀번호 재설정 업데이트
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (JWTUtil.isExpired(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "만료된 토큰입니다.");
        }
        String username = JWTUtil.getClaim(token);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    @Transactional
    public LoginResponseDTO refreshToken(String refreshToken) {
        if (JWTUtil.isExpiredRefreshToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");
        }

        String username = JWTUtil.getClaim(refreshToken);
        memberRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String newAccessToken = JWTUtil.getJWT(username);
        String newRefreshToken = JWTUtil.getRefreshToken(username);

        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }
}