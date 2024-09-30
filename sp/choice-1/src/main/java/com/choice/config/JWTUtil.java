package com.choice.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTUtil {
    // 액세스 토큰의 유효기간 30분 (밀리초 단위)
    private static final long ACCESS_TOKEN_MSEC = 30 * 60 * 1000L; // 30분

    // 리프레시 토큰의 유효기간 14일 (밀리초 단위)
    private static final long REFRESH_TOKEN_MSEC = 14 * 24 * 60 * 60 * 1000L; // 14일

    // JWT 토큰 서명에 사용되는 비밀 키
    private static String jwtKey;

    // JWT 클레임 이름
    private static final String CLAIM_NAME = "username";
    // 토큰의 접두사
    private static final String PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    public void setJwtKey(String key) {
        jwtKey = key;
    }

    // 액세스 및 리프레시 토큰을 생성하고 반환하는 메서드
    public static Map<String, Object> getJWTWithExpiration(String username) {
        Date now = new Date();
        Date accessExpiration = new Date(now.getTime() + ACCESS_TOKEN_MSEC); // 액세스 토큰 유효기간
        Date refreshExpiration = new Date(now.getTime() + REFRESH_TOKEN_MSEC); // 리프레시 토큰 유효기간

        // 액세스 토큰 생성
        String accessToken = PREFIX + JWT.create()
                .withClaim(CLAIM_NAME, username)
                .withExpiresAt(accessExpiration) // 만료 시간 설정
                .sign(Algorithm.HMAC256(jwtKey));
        log.info("액세스 토큰: {}", accessToken);
        String refreshToken = PREFIX + JWT.create()
                .withClaim(CLAIM_NAME, username)
                .withExpiresAt(refreshExpiration) // 만료 시간 설정
                .sign(Algorithm.HMAC256(jwtKey));
        log.info("리프레시 토큰: {}", refreshToken);
        // 액세스 토큰과 리프레시 토큰 만료 시간 정보를 함께 반환
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token", accessToken); // 액세스 토큰
        tokenData.put("accessExpiration", accessExpiration.getTime()); // 액세스 토큰 만료 시간
        tokenData.put("refreshToken", refreshToken); // 리프레시 토큰
        tokenData.put("refreshExpiration", refreshExpiration.getTime()); // 리프레시 토큰 만료 시간

        return tokenData;
    }

    // 주어진 토큰에서 사용자 이름 클레임을 추출하는 메서드
    public static String getClaim(String token) {
        String tok = getJWTSource(token);
        return JWT.require(Algorithm.HMAC256(jwtKey))
                .build()
                .verify(tok)
                .getClaim(CLAIM_NAME)
                .asString();
    }

    // 주어진 토큰이 만료되었는지 확인하는 메서드
    public static boolean isExpired(String token) {
        String tok = getJWTSource(token);
        return JWT.require(Algorithm.HMAC256(jwtKey))
                .build()
                .verify(tok)
                .getExpiresAt()
                .before(new Date());
    }

    // // 토큰에서 접두사 "Bearer "를 제거하는 메서드
    // public static String getJWTSource(String token) {
    // if (token.startsWith(PREFIX)) {
    // return token.replace(PREFIX, "");
    // }
    // return token;
    // }
    public static String getJWTSource(String token) {
        if (token == null) {
            return null;
        }
        token = token.trim();
        if (token.startsWith("\"") && token.endsWith("\"")) {
            token = token.substring(1, token.length() - 1);
        }
        if (token.startsWith(PREFIX)) {
            return token.substring(PREFIX.length());
        }
        return token;
    }

    // 리프레시 토큰이 만료되었는지 확인하는 메서드
    public static boolean isExpiredRefreshToken(String token) {
        try {
            String tok = getJWTSource(token);
            log.info("Validating refresh token: {}", tok);
            JWT.require(Algorithm.HMAC256(jwtKey))
                    .build()
                    .verify(tok);
            return false; // 토큰이 유효하면 만료되지 않았으므로 false 반환
        } catch (Exception e) {
            log.error("Refresh token validation error: ", e);
            return true; // 예외가 발생하면 토큰이 만료되었거나 유효하지 않으므로 true 반환
        }
    }

    // 이메일 인증 토큰 생성
    public static String getEmailVerificationToken(String username) {
        return PREFIX + JWT.create()
                .withClaim(CLAIM_NAME, username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24시간
                .sign(Algorithm.HMAC256(jwtKey));
    }

    // 비밀번호 재설정 토큰 생성
    public static String getPasswordResetToken(String username) {
        return PREFIX + JWT.create()
                .withClaim(CLAIM_NAME, username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30분
                .sign(Algorithm.HMAC256(jwtKey));
    }
}
