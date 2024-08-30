package com.fls.animecommunity.animesanctuary.controller.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.service.impl.MemberServiceImpl;
import com.fls.animecommunity.animesanctuary.service.interfaces.OAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LineLoginController {
	
	//DI
	private final OAuthService oAuthService;
	
	// 로그인 후 code와 state 값을 받아옴 , Callback URL
	@GetMapping("/callback")
	public String handleCallback(@RequestParam("code") String code, 
	                             @RequestParam("state") String state,
	                             HttpServletRequest request) {
	    
	    // 세션에서 저장된 state와 비교 (CSRF 보호)
	    String sessionState = getStateFromSessionOrCookie(request);
	    if (sessionState == null || !sessionState.equals(state)) {
	        // state 값이 다르면 에러 처리 (CSRF 공격 의심)
	        return "redirect:/error";
	    }

	    // code 값을 사용해 액세스 토큰 요청
	    String accessToken = oAuthService.getAccessToken(code);

	    // 액세스 토큰을 사용해 사용자 정보 조회 및 기타 처리
	    // 예: 사용자 정보를 가져와 DB에 저장하거나 세션에 로그인 상태로 저장

	    return "redirect:/home"; // 인증 완료 후 홈으로 리다이렉트
	}

	// 세션이나 쿠키에서 state 값을 가져옴
	private String getStateFromSessionOrCookie(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    return (session != null) ? (String) session.getAttribute("state") : null;
	}
	
	@GetMapping("/line-login")
	public String lineLogin(HttpServletRequest request) {
	    String clientId = "YOUR_CLIENT_ID"; // 실제 클라이언트 ID로 대체
	    String redirectUri = "http://localhost:9000/callback"; // 실제 콜백 URL로 대체
	    String state = generateRandomState(); // 무작위 state 생성 (CSRF 방지용)
	    String scope = "profile openid";
	    
	    String loginUrl = "https://access.line.me/oauth2/v2.1/authorize?response_type=code"
	                    + "&client_id=" + clientId
	                    + "&redirect_uri=" + redirectUri
	                    + "&state=" + state
	                    + "&scope=" + scope;

	    // state 값을 세션 또는 쿠키에 저장해둠 (콜백 시 검증용)
	    saveStateToSessionOrCookie(state, request);

	    return "redirect:" + loginUrl; // 사용자에게 LINE 로그인 페이지로 리다이렉트
	}
	
	// state 값을 세션 또는 쿠키에 저장
	private void saveStateToSessionOrCookie(String state, HttpServletRequest request) {
	    HttpSession session = request.getSession();
	    session.setAttribute("state", state);
	}

	// 난수 생성
	private String generateRandomState() {
	    SecureRandom secureRandom = new SecureRandom();
	    byte[] randomBytes = new byte[24];
	    secureRandom.nextBytes(randomBytes);
	    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}

	// state 값이 유효한지 확인
	private boolean isValidState(String state) {
	    return state != null && !state.isEmpty();
	}

}
