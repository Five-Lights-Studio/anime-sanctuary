package com.fls.animecommunity.animesanctuary.config;
// NOTE: import Firebase from config as it belonging to the same package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Collections;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import com.fls.animecommunity.animesanctuary.service.impl.MemberService;

@Configuration
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;

    public SecurityConfig(MemberRepository memberRepository, NoteRepository noteRepository) {
        this.memberRepository = memberRepository;
        this.noteRepository = noteRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**", "/login", "/resources/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/notes/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/notes/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new FirebaseAuthenticationFilter(new MemberService(memberRepository, noteRepository, passwordEncoder())), 
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
