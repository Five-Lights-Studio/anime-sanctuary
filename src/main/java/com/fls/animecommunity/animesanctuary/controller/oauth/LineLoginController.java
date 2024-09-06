package com.fls.animecommunity.animesanctuary.controller.oauth;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fls.animecommunity.animesanctuary.dto.LoginRequest;
import com.fls.animecommunity.animesanctuary.model.member.GenderType;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.MemberService;
import com.fls.animecommunity.animesanctuary.service.interfaces.OAuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth/callback")
public class LineLoginController {

	private final OAuthService oAuthService;
	private final MemberService memberService;
	private final MemberRepository memberRepository;

	private final WebClient webClient = WebClient.create("http://localhost:9000");

	private String channelID = "2006160018";
	private static int i = 78;

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
		Mono<String> validityResponse = webClient.get().uri(
				uriBuilder -> uriBuilder.path("/oauth2/v2.1/verify").queryParam("access_token", accessToken).build())
				.retrieve().bodyToMono(String.class);

		// 동기적으로 block()을 사용하여 응답을 받음
		String validityResult = validityResponse.block();
		log.info("validityResponse : {}", validityResult);

		// 응답을 JSON 형태로 파싱하여 client_id 및 expires_in 값을 검증
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> validityMap = mapper.readValue(validityResult, Map.class);

		String clientId = (String) validityMap.get("client_id");
		int expiresIn = (Integer) validityMap.get("expires_in");

		if (!clientId.equals(channelID) || expiresIn <= 0) {
			return "redirect:/error";
		}

		// Get user profile
		Mono<String> profileResponse = webClient.get().uri("/v2/profile").headers(headers -> {
			headers.set("Authorization", "Bearer " + accessToken);
		}).retrieve().bodyToMono(String.class);

		// 동기적으로 프로필 정보를 가져옴
		String profileResult = profileResponse.block();
		log.info("profileResponse : {}", profileResult);

		// 프로필 데이터를 JSON으로 파싱
		Map<String, Object> profileMap = mapper.readValue(profileResult, Map.class);
		String userId = (String) profileMap.get("userId");
		String displayName = (String) profileMap.get("displayName");

		log.info("success: 프로필 데이터를 JSON으로 파싱");

		// 새로운 사용자 생성 (User profile)
		Member member = new Member();
		String username = "testUser-" + UUID.randomUUID().toString();
		member.setUsername(username);
		i++;
		member.setPassword("aaaa"); // 기본 비밀번호 설정
		member.setName(displayName);
		member.setBirth(LocalDate.of(1970, 1, 1));
		member.setGender(GenderType.MALE);
		memberRepository.save(member); // 저장
		log.info("success: Create new user");
		log.info("member: {}", member);

		// login하기 post요청을 보내서 세션생성
		WebClient webClientLogin = WebClient.create("http://localhost:9000");
		LoginRequest loginRequest = new LoginRequest(username, "aaaa");

		// 로그인 후 /welcome으로 리다이렉트 처리
		return webClientLogin.post()
				.uri("/api/members/login")
				.bodyValue(loginRequest) // loginRequest 객체를 JSON으로 직렬화하여 요청 본문으로 전송
				.retrieve()
				.onStatus(status -> status.is4xxClientError(), ClientResponse::createException) // 4xx 에러 처리
				.onStatus(status -> status.is5xxServerError(), ClientResponse::createException) // 5xx 에러 처리
				.bodyToMono(String.class)
				.flatMap(response -> {
					// 로그인 성공 시 /welcome 경로로 리다이렉트
					log.info("Login successful. Redirecting to /welcome");
					return Mono.just("redirect:/welcome");
				})
				.onErrorResume(error -> {
					// 에러가 발생하면 /error로 리다이렉트
					log.error("Login failed: " + error.getMessage());
					return Mono.just("redirect:/error");
				}).block(); // 동기적으로 수행
	}

	// error ACCESS_DENIED
	@GetMapping("?error=ACCESS_DENIED/")
	public String ACCESS_DENIED(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect:/";
	}

	// error INVALID_REQUEST
	@GetMapping("?error=INVALID_REQUEST/")
	public String INVALID_REQUEST(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect:/";
	}

	// error INVALID_SCOPE
	@GetMapping("?error=INVALID_SCOPE/")
	public String INVALID_SCOPE(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect:/";
	}

	// error SERVER_ERROR
	@GetMapping("?error=SERVER_ERROR/")
	public String SERVER_ERROR(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect:/";
	}

	// error UNSUPPORTED_RESPONSE_TYPE
	@GetMapping("?error=UNSUPPORTED_RESPONSE_TYPE/")
	public String UNSUPPORTED_RESPONSE_TYPE(@RequestParam String error, @RequestParam String error_description) {
		log.info("error : {}", error);
		log.info("error_description : {}", error_description);
		return "redirect:/";
	}

}
