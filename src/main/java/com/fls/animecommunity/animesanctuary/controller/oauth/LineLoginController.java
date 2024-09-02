package com.fls.animecommunity.animesanctuary.controller.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.service.interfaces.OAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LineLoginController {

    private final OAuthService oAuthService;

    // 로그인 후 code와 state 값을 받아옴 , Callback URL
    @GetMapping("/auth/callback")
    public String handleCallback(@RequestParam("code") String code, 
                                 @RequestParam("state") String state,
                                 HttpServletRequest request) {
        // CSRF 보호를 위한 state 값 검증
        String sessionState = "xyzABC123";
        if (sessionState == null || !sessionState.equals(state)) {
            // state 값이 다르면 에러 처리 (CSRF 공격 의심)
            log.warn("Invalid state parameter: received={}, expected={}", state, sessionState);
            return "redirect:/error";
        }

        // code 값을 사용해 액세스 토큰 요청
        String accessToken = oAuthService.getAccessToken(code);

        // 액세스 토큰을 사용해 사용자 정보 조회 및 기타 처리
        log.info("Access token received: {}", accessToken);

        return "redirect:/home"; // 인증 완료 후 홈으로 리다이렉트
    }
    
    
    
    
    
    // 세션이나 쿠키에서 state 값을 가져옴
    private String getStateFromSessionOrCookie(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("state");
        }
        return null;
    }
}
