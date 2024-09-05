package com.choice.config;

import java.io.IOException;
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
        String srcToken = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 토큰 가져오기
        if (srcToken == null || !srcToken.startsWith("Bearer ")) { // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘김
            filterChain.doFilter(request, response); // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘김
            return;
        }

        String jwtToken = srcToken.replace("Bearer ", ""); // Bearer 부분을 제거하여 실제 토큰 값 추출

        try { // JWT 토큰 검증
            if (JWTUtil.isExpired(jwtToken)) { // 토큰이 만료되었으면 응답 상태를 401 Unauthorized로 설정
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired");
                return;
            }

            String username = JWTUtil.getClaim(jwtToken); // JWT 토큰에서 사용자 이름(username) 추출
            Optional<Member> opt = memberRepository.findByUsername(username); // 사용자 이름으로 멤버 조회

            if (!opt.isPresent()) { // 멤버가 없으면 응답 상태를 401 Unauthorized로 설정
                filterChain.doFilter(request, response); // 다음 필터로 넘김
                return;
            }

            Member findMember = opt.get();
            User user = new User(findMember.getUsername(), findMember.getPassword(),
                    AuthorityUtils.createAuthorityList(findMember.getRole().name())); // 사용자 이름, 비밀번호, 권한 설정

            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()); // 인증 객체
                                                                                                              // 생성
            SecurityContextHolder.getContext().setAuthentication(auth); // 인증 객체를 SecurityContextHolder에 설정

            filterChain.doFilter(request, response); // 다음 필터로 넘김
        } catch (Exception e) { // 예외 발생 시 응답 상태를 401 Unauthorized로 설정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
        }
    }
}
