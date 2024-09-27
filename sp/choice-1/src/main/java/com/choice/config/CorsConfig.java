package com.choice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // CORS 설정을 추가하는 메서드
        registry.addMapping("/**") // 모든 경로에 대해 CORS 설정 적용
                // .allowedOrigins("http://10.125.121.187:3000", "http://10.125.121.181:3000",
                // "http://10.125.121.187:5000",
                // "http://10.125.121.182:5000") // 특정
                // TODO ip주소 확인하고 cors 설정 수정
                .allowedOrigins("*") // 특정
                // 도메인에서의
                // 요청을
                // 허용
                .allowedMethods("*") // 허용할 HTTP 메서드 지정
                .allowedHeaders("*"); // 모든 헤더를 허용
        // .allowCredentials(true); // 자격 증명(쿠키, Authorization 헤더 등) 포함 허용
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 특정 폴더에 저장되어 있는 리소스 호출
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:C:\\workspace_pj2\\back\\images\\");
        registry.addResourceHandler("/qboard/**")
        		.addResourceLocations("file:C:\\workspace_pj2\\back\\qboard\\");
    }
}