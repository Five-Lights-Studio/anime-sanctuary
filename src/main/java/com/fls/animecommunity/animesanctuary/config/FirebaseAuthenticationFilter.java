package com.fls.animecommunity.animesanctuary.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.fls.animecommunity.animesanctuary.service.impl.MemberService;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class FirebaseAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final MemberService memberService;

    public FirebaseAuthenticationFilter(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);  // Token after "Bearer "
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null; // We don't need this for Firebase
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String token = (String) getPreAuthenticatedPrincipal(request);

        if (token != null) {
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String email = decodedToken.getEmail();

                // Fetch the user from the database based on the email
                Member member = memberService.findByEmail(email).orElseGet(() -> {
                    // If the user doesn't exist, register a new one
                    Member newMember = new Member();
                    newMember.setEmail(email);
                    newMember.setName(decodedToken.getName());
                    newMember.setGoogleAccount(true);  // This is a Google account
                    return memberService.save(newMember);
                });

                // Authenticate the user
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(member, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (FirebaseAuthException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
