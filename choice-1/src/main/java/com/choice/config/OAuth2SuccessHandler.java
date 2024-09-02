package com.choice.config;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.choice.auth.entity.Member;
import com.choice.auth.entity.Role;
import com.choice.auth.repository.MemberRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j  
@RequiredArgsConstructor 
@Component  
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepo;  // MemberRepository 주입
    private final PasswordEncoder encoder;  // PasswordEncoder 주입

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                         HttpServletResponse response, 
                                         Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2SuccessHandler: 인증 성공 후 처리");

        // 인증된 사용자 정보 가져오기
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String username = CustomMyUtil.getUsernameFromOAuth2User(user); // OAuth2 사용자로부터 사용자 이름 추출

        if (username == null) {
            log.error("onAuthenticationSuccess: OAuth2 사용자로부터 사용자 이름을 생성할 수 없습니다!");
            throw new ServletException("OAuth2 사용자로부터 사용자 이름을 생성할 수 없습니다!");
        }
        log.info("onAuthenticationSuccess: 사용자 이름 " + username);

        // 사용자 이름으로 기존 회원 찾기 또는 새 회원 생성
        Member member = memberRepo.findByUsername(username)
            .orElseGet(() -> {
                // 새 회원 생성 및 저장
                Member newMember = Member.builder()
                		.username(username) // 사용자 이름 설정
                        .password(encoder.encode("1a2s3d4f")) // 임시 비밀번호 설정, 보안을 위해 실제 환경에서는 더 나은 방법 사용
                        .nickname(username) // 닉네임을 사용자 이름으로 설정
                        .role("MEMBER") // 기본 역할 설정, "MEMBER"와 같이 문자열 사용
                        .joinDate(LocalDateTime.now()) // 가입일자 설정
                        .editedDate(LocalDateTime.now()) // 수정일자 설정
                        .build();
                return memberRepo.save(newMember); // 새 회원 저장
            });

        // JWT 토큰 생성
        String jwtToken = JWTUtil.getJWT(username);
        // 리다이렉트 URL 생성
        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + jwtToken 
        					+ "&id=" + member.getUserId()
        					+ "&username=" + member.getUsername()
        					+ "&nickname=" + member.getNickname();
        response.sendRedirect(redirectUrl); // 클라이언트를 리다이렉트
    }
}
