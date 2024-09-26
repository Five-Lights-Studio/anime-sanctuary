package com.fls.animecommunity.animesanctuary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/*
 * CORS(Cross-Origin Resource Sharing) 설정
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://127.0.0.1:5501")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // Content Negotiation 설정
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(true)                      // URL 파라미터로 Content-Type 설정
            .parameterName("mediaType")                // URL 파라미터 이름을 설정
            .ignoreAcceptHeader(false)                 // Accept 헤더를 무시하지 않음
            .useRegisteredExtensionsOnly(false)        // 확장자 기반 미디어 타입 결정 비활성화
            .defaultContentType(MediaType.APPLICATION_JSON)  // 기본 응답 타입을 JSON으로 설정
            .mediaType("jfif", MediaType.IMAGE_JPEG)         // jfif 확장자를 image/jpeg로 매핑
            .mediaType("json", MediaType.APPLICATION_JSON);  // json 확장자를 JSON으로 매핑
    }
}
