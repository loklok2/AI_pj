package com.choice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.choice.admin.util.SessionListener;
import com.choice.auth.repository.MemberRepository;

import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // private final OAuth2SuccessHandler successHandler;
    private final MemberRepository memberRepository;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        // .requestMatchers("/api/admin/**", "/api/sale/**",
                        // "/api/visitors/**").hasRole("ADMIN")
                        // .requestMatchers("/api/comment/**", "/orders/**",
                        // "/qboard/**").authenticated()
                        // .requestMatchers("/api/auth/**", "/api/cart/**",
                        // "/api/product/**").permitAll()
                        .anyRequest().permitAll()) // 그 외 요청은 모두 허용
                .formLogin(form -> form.disable()) // 기본 로그인 페이지 비활성화
                // .oauth2Login(oauth2 -> oauth2.successHandler(successHandler)) // OAuth2 로그인
                // 성공 핸들러 설정
                .addFilterBefore(new JWTAuthorizationFilter(memberRepository),
                        UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
        http.cors(c -> {
        }); // CORS 설정
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // 기본 인증 제공자를 사용하여 AuthenticationManager 설정 및 반환
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    HttpSessionListener httpSessionListener() {
        // 세션 리스너 설정
        return new SessionListener();
    }

    @Bean
    // 스케줄러 설정
    public TaskScheduler taskScheduler() {
        // 스케줄러 설정
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 스케줄러 풀 크기 설정
        scheduler.setPoolSize(10);
        // 스케줄러 스레드 이름 설정
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler-");
        return scheduler;
    }
}
