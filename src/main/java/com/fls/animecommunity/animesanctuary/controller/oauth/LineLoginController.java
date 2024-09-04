package com.fls.animecommunity.animesanctuary.controller.oauth;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fls.animecommunity.animesanctuary.model.member.GenderType;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.MemberService;
import com.fls.animecommunity.animesanctuary.service.interfaces.OAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth/callback")
public class LineLoginController {

	private final OAuthService oAuthService;
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private String channelID = "2006160018";

	// 로그인 후 code와 state 값을 받아옴 , Callback URL
	@GetMapping
	public String handleCallback(@RequestParam("code") String code, @RequestParam("state") String state,
	                             HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
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

	    // Verify access token validity
	    WebClient webClient = WebClient.create("https://api.line.me");

	    // 유효성 검증 API 호출
	    Mono<String> validityResponse = webClient.get()
	            .uri(uriBuilder -> uriBuilder
	                    .path("/oauth2/v2.1/verify")
	                    .queryParam("access_token", accessToken)
	                    .build())
	            .retrieve()
	            .bodyToMono(String.class);

	    // 동기적으로 block()을 사용하여 응답을 받음
	    String validityResult = validityResponse.block();
	    log.info("validityResponse : {}", validityResult);

	    // 응답을 JSON 형태로 파싱하여 client_id 및 expires_in 값을 검증
	    // (JSON 파싱을 위한 ObjectMapper를 사용하거나, 필요한 값을 직접 파싱해야 함)
	    // 가정: validityResult는 JSON 문자열이라고 가정하고 처리

	    // 예시로 JSON 파싱을 위해 ObjectMapper를 사용
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String, Object> validityMap = mapper.readValue(validityResult, Map.class);

	    String clientId = (String) validityMap.get("client_id");
	    int expiresIn = (Integer) validityMap.get("expires_in");

	    if (!clientId.equals(channelID) || expiresIn <= 0) {
	        return "redirect:/error";
	    }

	    // Get user profile
	    Mono<String> profileResponse = webClient.get()
	            .uri("/v2/profile")
	            .headers(headers -> {
	                headers.set("Authorization", "Bearer " + accessToken);
	            })
	            .retrieve()
	            .bodyToMono(String.class);

	    // 동기적으로 프로필 정보를 가져옴
	    String profileResult = profileResponse.block();
	    log.info("profileResponse : {}", profileResult);

	    // 프로필 데이터를 JSON으로 파싱
	    Map<String, Object> profileMap = mapper.readValue(profileResult, Map.class);
	    String userId = (String) profileMap.get("userId");
	    String displayName = (String) profileMap.get("displayName");
	    
	    log.info("sucess: 프로필 데이터를 JSON으로 파싱");
	    
	    // Create new user (User profile)
	    Member member = new Member();
	    
	    String username = userId;

	    if (username.length() > 50) {  // 데이터베이스 제한에 맞춰 50자로 제한
	        username = username.substring(0, 50);  // 50자까지만 잘라서 저장
	    }
	    
	    
//	    member.setUsername(userId);
	    member.setUsername(userId);
	    member.setPassword("aaaa");
	    member.setName(displayName);
	    member.setBirth(LocalDate.of(1970, 1, 1));
	    member.setGender(GenderType.MALE);
	    memberRepository.save(member);
	    log.info("sucess: Create new user");
	    log.info("member: {}",member);
//	    Member member = memberRepository.findByLineUserId(lineUserId);
//        if (member == null) {
//            member = new Member();
//            member.setLineUserId(lineUserId);
//            member.setUsername((String) userProfile.get("displayName"));
//            member.setProfileImage((String) userProfile.get("pictureUrl"));
//            memberRepository.save(member);
//        }

	    return "login"; 
	}


	// error ACCESS_DENIED
	@GetMapping("?error=ACCESS_DENIED/")
	public String ACCESS_DENIED(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect://";
	}

	// error INVALID_REQUEST
	@GetMapping("?error=INVALID_REQUEST/")
	public String INVALID_REQUEST(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect://";
	}

	// error INVALID_SCOPE
	@GetMapping("?error=INVALID_SCOPE/")
	public String INVALID_SCOPE(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect://";
	}

	// error SERVER_ERROR
	@GetMapping("?error=SERVER_ERROR/")
	public String SERVER_ERROR(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect://";
	}

	// error UNSUPPORTED_RESPONSE_TYPE
	@GetMapping("?error=UNSUPPORTED_RESPONSE_TYPE/")
	public String UNSUPPORTED_RESPONSE_TYPE(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect://";
	}

}
