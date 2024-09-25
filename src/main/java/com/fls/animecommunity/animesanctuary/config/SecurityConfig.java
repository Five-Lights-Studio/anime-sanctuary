package com.fls.animecommunity.animesanctuary.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import com.fls.animecommunity.animesanctuary.service.impl.MemberService;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;

    // 생성자를 통해 MemberRepository + NoteRepository를 주입받음
    public SecurityConfig(MemberRepository memberRepository, NoteRepository noteRepository) {
        this.memberRepository = memberRepository;
        this.noteRepository = noteRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
            	// 회원가입 및 로그인은 인증 없이 접근 가능
                .requestMatchers("/api/members/register", "/api/members/login").permitAll()
                // 그 외의 /api/members/** 경로는 인증된 사용자만 접근 가능
                .requestMatchers("/api/members/**").authenticated()
                // 노트 조회와 검색은 GET 요청에 한해서 인증 없이 접근 가능
                .requestMatchers(HttpMethod.GET, "/api/notes", "/api/notes/search").permitAll()
                // GET 메소드에 대해서만 노트 조회 허용, 그 외 POST, PUT, DELETE 등은 인증 필요
                .requestMatchers("/api/notes/**").authenticated()  // 노트 생성, 수정, 삭제 등은 인증 필요

                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()  // Swagger 관련 URL 접근 허용
                .anyRequest().authenticated()  // 나머지 모든 요청은 인증 필요
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(this.oauth2UserService())
                )
                .defaultSuccessUrl("http://localhost:5501/index.html")  // 성공 후 리다이렉트할 URL
                .failureUrl("http://localhost:5501/login.html")  // 로그인 실패 시 리다이렉트할 URL
            );

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5501"); // 허용할 도메인 추가
        configuration.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return userRequest -> {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

            String provider = userRequest.getClientRegistration().getRegistrationId();  // "google"
            String providerId = oAuth2User.getAttribute("sub");  // 구글의 사용자 ID
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            // MemberService 생성 시 NoteRepository도 전달
            MemberService memberService = new MemberService(memberRepository, noteRepository, passwordEncoder());
            Member member = memberService.registerOrLoginWithSocial(name, email, provider, providerId);

            return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                oAuth2User.getAttributes(),
                "email"  // 기본 키
            );
        };
    }
}


