package com.fls.animecommunity.animesanctuary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	 @Bean
	 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (실제 환경에서는 필요한 경우만 비활성화)
            .authorizeHttpRequests(auth -> auth  // 새로운 방식으로 권한 설정
                .requestMatchers("/api/members/**").permitAll()  // /api/members/** 경로는 인증 없이 접근 가능
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                //apitest를 위해 추가
                .requestMatchers("/api/**").permitAll()  // /api/notes/** 경로 인증 없이 접근 가능
                .requestMatchers("/", "/index.html", "/static/**", "/public/**").permitAll()
                .anyRequest().authenticated()  // 그 외 모든 요청은 인증 필요
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
    }
}
