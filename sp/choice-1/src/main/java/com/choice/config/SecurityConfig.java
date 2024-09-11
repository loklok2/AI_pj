package com.choice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.choice.auth.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2SuccessHandler successHandler;
    private final MemberRepository memberRepository;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/qboard", "/api/qboard/{id}",
                                "/api/qboard/{qboardId}/images")
                        .permitAll()
                        .requestMatchers("/api/qboard/**").authenticated()
                        .requestMatchers("/api/comments/qboard/{qboardId}").permitAll()
                        .requestMatchers("/api/comments/**").authenticated()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/api/recommendation/**").permitAll()
                        .anyRequest().permitAll())
                .formLogin(form -> form.disable()) // 기본 로그인 페이지 비활성화
                .oauth2Login(oauth2 -> oauth2.successHandler(successHandler)) // OAuth2 로그인 성공 핸들러 설정
                .addFilterBefore(new JWTAuthorizationFilter(memberRepository),
                        UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // 기본 인증 제공자를 사용하여 AuthenticationManager 설정 및 반환
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}
