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
import com.fls.animecommunity.animesanctuary.service.MemberService;

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
                // 회원가입과 로그인 경로는 인증 없이 접근 가능
                .requestMatchers("/api/notes","/api/notes/{noteId}","/api/members/register", "/api/members/login","/api/members/logout","/api/categories","/api/admin/categories").permitAll()
                // OAuth2 경로는 인증 필요
                .requestMatchers("/api/members/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/notes", "/api/notes/search").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(this.oauth2UserService())
                )
                .defaultSuccessUrl("http://localhost:5501/index.html")
                .failureUrl("http://localhost:5501/login.html")
            );

        return http.build();
    }

    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*")); // 허용할 도메인 추가
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


