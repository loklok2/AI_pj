package com.choice.config;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// JWT 토큰을 통해 사용자 인증 처리
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository; // MemberRepository 주입

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 토큰 가져오기
        String refreshToken = request.getHeader("Refresh-Token"); // 요청 헤더에서 Refresh-Token 토큰 가져오기

        if (accessToken == null || !accessToken.startsWith("Bearer ")) { // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘김
            filterChain.doFilter(request, response); // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘김
            return;
        }

        String jwtToken = JWTUtil.getJWTSource(accessToken); // JWT 토큰 가져오기

        try { // JWT 토큰 검증
            if (JWTUtil.isExpired(jwtToken)) {
                if (refreshToken != null && !JWTUtil.isExpiredRefreshToken(refreshToken)) {
                    // 리프레시 토큰으로 새 토큰 발급
                    String username = JWTUtil.getClaim(refreshToken);
                    Map<String, Object> newTokens = JWTUtil.getJWTWithExpiration(username);
                    String newAccessToken = (String) newTokens.get("token");
                    String newRefreshToken = (String) newTokens.get("refreshToken");

                    response.setHeader("Authorization", newAccessToken);
                    response.setHeader("Refresh-Token", newRefreshToken);
                    jwtToken = JWTUtil.getJWTSource(newAccessToken);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token has expired and refresh token is invalid");
                    return;
                }
            }

            String username = JWTUtil.getClaim(jwtToken);
            Optional<Member> opt = memberRepository.findByUsername(username);

            if (opt.isPresent()) {
                Member findMember = opt.get();
                User user = new User(findMember.getUsername(), findMember.getPassword(),
                        AuthorityUtils.createAuthorityList(findMember.getRole().name())); // 사용자 이름, 비밀번호, 권한 설정

                Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()); // 인증
                                                                                                                  // 객체
                // 생성
                SecurityContextHolder.getContext().setAuthentication(auth); // 인증 객체를 SecurityContextHolder에 설정

                filterChain.doFilter(request, response); // 다음 필터로 넘김
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("User not found");
            }
        } catch (Exception e) { // 예외 발생 시 응답 상태를 401 Unauthorized로 설정
            System.out.println("Exception in JWTAuthorizationFilter: " + e.getMessage()); // 로깅 추가
            e.printStackTrace(); // 스택 트레이스 출력
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token: " + e.getMessage());
        }
    }
}
