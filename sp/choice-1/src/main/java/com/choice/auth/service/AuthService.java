package com.choice.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.choice.auth.dto.LoginRequestDTO;
import com.choice.auth.dto.LoginResponseDTO;
import com.choice.auth.dto.MemberSignupRequestDTO;
import com.choice.auth.entity.Member;
import com.choice.auth.entity.Token;
import com.choice.auth.entity.TokenType;
import com.choice.auth.repository.MemberRepository;
import com.choice.auth.repository.TokenRepository;
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

    @Autowired
    private TokenRepository tokenRepository;

    // 회원가입
    @Transactional
    public void registerUser(MemberSignupRequestDTO requestDto) {
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
        String birthDate = residentRegistrationNumber.substring(0, 6);
        char genderCode = residentRegistrationNumber.charAt(6);
        String gender = (genderCode == '1' || genderCode == '3') ? "남자" : "여자"; // 성별 구분

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 이메일 인증 토큰 생성
        String verificationToken = UUID.randomUUID().toString();

        // 사용자 생성 및 저장
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .birthDate(birthDate)
                .gender(gender)
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .role("MEMBER")
                .joinDate(LocalDateTime.now())
                .editedDate(LocalDateTime.now())
                .build();

        // 회원 정보 저장
        Member savedMember = memberRepository.save(member);

        // 이메일 인증 토큰 생성 및 저장
        Token token = Token.builder()
                .userId(savedMember.getUserId())
                .tokenType(TokenType.EMAIL_VERIFICATION.name())
                .tokenValue(verificationToken)
                .createDate(LocalDateTime.now()) // 생성 날짜 추가
                .expiredDate(LocalDateTime.now().plusHours(24)) // 만료 날짜 추가 (예: 24시간 후)
                .build();
        tokenRepository.save(token);

        emailService.sendVerificationEmail(member.getEmail(), verificationToken); // 이메일 인증 메일 발송
    }

    // 로그인
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Member member = memberRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = JWTUtil.getJWT(member.getUsername());
        String refreshToken = JWTUtil.getRefreshToken(member.getUsername());

        // 기존 토큰 삭제
        tokenRepository.deleteByUserIdAndTokenTypeIn(member.getUserId(),
                Arrays.asList(TokenType.ACCESS.name(), TokenType.REFRESH.name()));

        // 새 토큰 저장
        Token accessTokenEntity = Token.builder()
                .userId(member.getUserId())
                .tokenType(TokenType.ACCESS.name())
                .tokenValue(accessToken)
                .createDate(LocalDateTime.now())
                .expiredDate(LocalDateTime.now().plusMinutes(30)) // 예: 30분 후 만료
                .build();
        tokenRepository.save(accessTokenEntity);

        Token refreshTokenEntity = Token.builder()
                .userId(member.getUserId())
                .tokenType(TokenType.REFRESH.name())
                .tokenValue(refreshToken)
                .createDate(LocalDateTime.now())
                .expiredDate(LocalDateTime.now().plusDays(14)) // 예: 14일 후 만료
                .build();
        tokenRepository.save(refreshTokenEntity);

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    // 이메일 인증
    @Transactional
    public boolean verifyEmail(String token) {
        Token verificationToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 토큰입니다."));

        if (!verificationToken.getTokenType().equals(TokenType.EMAIL_VERIFICATION.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 토큰 유형입니다.");
        }

        // 토큰 삭제만 수행
        tokenRepository.delete(verificationToken);
        return true;
    }

    // 24시간 이후에 인증 토큰이 만료되면 사용자 계정 삭제
    @Scheduled(fixedRate = 86400000) // 24시간마다 실행 (24 * 60 * 60 * 1000 ms)
    @Transactional
    public void deleteUnverifiedMembers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // 24시간 전 시간 설정
        List<Token> expiredTokens = tokenRepository.findByTokenTypeAndCreateDate(
                TokenType.EMAIL_VERIFICATION.name(), cutoffTime); // 인증 토큰 조회

        for (Token token : expiredTokens) {
            memberRepository.deleteById(token.getUserId()); // 회원 삭제
            tokenRepository.delete(token); // 토큰 삭제
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

        String resetToken = generateResetToken();
        Token token = Token.builder()
                .userId(member.getUserId()) // 회원 아이디
                .tokenType(TokenType.PASSWORD_RESET.name()) // 토큰 유형
                .tokenValue(resetToken) // 토큰 값
                .createDate(LocalDateTime.now()) // 생성 날짜
                .expiredDate(LocalDateTime.now().plusHours(24)) // 만료 날짜
                .build();
        tokenRepository.save(token); // 토큰 저장

        emailService.sendPasswordResetEmail(member.getEmail(), resetToken); // 비밀번호 재설정 메일 발송
    }

    // 비밀번호 재설정 토큰 생성
    private String generateResetToken() {
        return UUID.randomUUID().toString(); // 랜덤 토큰 생성
    }

    // 비밀번호 재설정 업데이트
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Token resetToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."));

        if (resetToken.getExpiredDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "만료된 토큰입니다.");
        }

        Member member = memberRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        member.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 업데이트
        memberRepository.save(member); // 회원 정보 저장

        tokenRepository.delete(resetToken); // 토큰 삭제
    }
}
