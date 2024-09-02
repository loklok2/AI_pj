package com.choice.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository; // MemberRepository 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // JWT 토큰을 통해 사용자 인증 처리
        String srcToken = request.getHeader("Authorization"); // 요청 헤더에서 Authorization 토큰 가져오기
        if (srcToken == null || !srcToken.startsWith("Bearer ")) { 
            filterChain.doFilter(request, response); // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 넘김
            return;            
        }

        String jwtToken = srcToken.replace("Bearer ", ""); // Bearer 부분을 제거하여 실제 토큰 값 추출
        
        // JWT 토큰에서 사용자 이름(username) 추출
        String username = JWT.require(Algorithm.HMAC256("edu.pnu.jwtkey"))
                             .build()
                             .verify(jwtToken)
                             .getClaim("username")
                             .asString();
        
        // 사용자 이름으로 데이터베이스에서 사용자 정보 조회
        Optional<Member> opt = memberRepository.findByUsername(username); 
        if (!opt.isPresent()) { 
            filterChain.doFilter(request, response); // 사용자가 존재하지 않으면 다음 필터로 넘김
            return;
        }
        
        Member findMember = opt.get(); // 사용자 정보 가져오기
        
        // 사용자 정보로 Spring Security User 객체 생성
        User user = new User(findMember.getUsername(), findMember.getPassword(), 
                AuthorityUtils.createAuthorityList(findMember.getRole()));
        
        // 인증 객체 생성 후 SecurityContext에 설정
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        filterChain.doFilter(request, response); // 요청을 다음 필터로 넘김
    }
}
