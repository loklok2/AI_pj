package com.choice.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
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

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 이메일 인증 토큰 생성
        String verificationToken = UUID.randomUUID().toString();

        // 사용자 생성 및 저장
        Member member = Member.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .birthDate(requestDto.getBirthDate())
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .role("MEMBER")
                .joinDate(LocalDateTime.now())
                .editedDate(LocalDateTime.now())
                .build();

        Member savedMember = memberRepository.save(member);

        Token token = Token.builder()
                .userId(savedMember.getUserId())
                .tokenType(TokenType.EMAIL_VERIFICATION.name())
                .tokenValue(verificationToken)
                .build();
        tokenRepository.save(token);

        emailService.sendVerificationEmail(member.getEmail(), verificationToken);
    }

    // 로그인
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<Member> optionalMember = memberRepository.findByUsername(loginRequestDTO.getUsername());
        if (optionalMember.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
        }

        Member member = optionalMember.get();

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = JWTUtil.getJWT(member.getUsername());
        String refreshToken = JWTUtil.getRefreshToken(member.getUsername());

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

        tokenRepository.delete(verificationToken);
        return true;
    }

    // 24시간 이후에 인증 토큰이 만료되면 사용자 계정 삭제
    @Scheduled(fixedRate = 86400000) // 24시간마다 실행 (24 * 60 * 60 * 1000 ms)
    @Transactional
    public void deleteUnverifiedMembers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Token> expiredTokens = tokenRepository.findByTokenTypeAndCreateDate(
                TokenType.EMAIL_VERIFICATION.name(), cutoffTime);

        for (Token token : expiredTokens) {
            memberRepository.deleteById(token.getUserId());
            tokenRepository.delete(token);
        }
    }

    // 아이디 찾기
    public String findUsername(String name, String email) {
        return memberRepository.findByNameAndEmail(name, email)
                .map(Member::getUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 사용자를 찾을 수 없습니다."));
    }

    // 비밀번호 재설정 이메일 발송
    public void requestPasswordReset(String username, String email) {
        Member member = memberRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "일치하는 사용자를 찾을 수 없습니다."));

        String resetToken = generateResetToken();
        Token token = Token.builder()
                .userId(member.getUserId())
                .tokenType(TokenType.PASSWORD_RESET.name())
                .tokenValue(resetToken)
                .createDate(LocalDateTime.now())
                .expiredDate(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepository.save(token);

        emailService.sendPasswordResetEmail(member.getEmail(), resetToken);
    }

    // 비밀번호 재설정 토큰 생성
    private String generateResetToken() {
        return UUID.randomUUID().toString();
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

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);

        tokenRepository.delete(resetToken);
    }
}
