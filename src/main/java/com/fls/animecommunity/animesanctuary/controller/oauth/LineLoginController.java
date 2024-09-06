package com.fls.animecommunity.animesanctuary.controller.oauth;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fls.animecommunity.animesanctuary.model.member.GenderType;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.OAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import java.util.HashMap;

@RestController
@RequestMapping("/auth/callback")
@RequiredArgsConstructor
@Slf4j
public class LineLoginController {

    private final OAuthService oAuthService;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();  // RestTemplate 생성

    private String channelID = "2006160018";

    @GetMapping
    public String handleCallback(@RequestParam("code") String code, @RequestParam("state") String state) throws JsonProcessingException {
        // state 값 검증 생략 가능
        String sessionState = "xyzABC123";
        if (!sessionState.equals(state)) {
            return "redirect:/error";
        }

        // 액세스 토큰 요청
        String accessToken = oAuthService.getAccessToken(code);
        log.info("Access token received: {}", accessToken);

        // 액세스 토큰 유효성 검증 (RestTemplate 사용)
        String verifyUrl = "https://api.line.me/oauth2/v2.1/verify?access_token=" + accessToken;
        ResponseEntity<String> validityResponse = restTemplate.getForEntity(verifyUrl, String.class);
        log.info("validityResponse: {}", validityResponse.getBody());

        // JSON 파싱하여 client_id 및 expires_in 검증
        ObjectMapper mapper = new ObjectMapper();  // ObjectMapper 선언 수정
        Map<String, Object> validityMap = mapper.readValue(validityResponse.getBody(), Map.class);

        String clientId = (String) validityMap.get("client_id");
        int expiresIn = (Integer) validityMap.get("expires_in");

        if (!clientId.equals(channelID) || expiresIn <= 0) {
            return "redirect:/error";
        }

        // 사용자 프로필 정보 요청 (RestTemplate에서 헤더 추가)
        String profileUrl = "https://api.line.me/v2/profile";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> profileResponse = restTemplate.exchange(profileUrl, HttpMethod.GET, entity, String.class);
        log.info("profileResponse: {}", profileResponse.getBody());

        // 프로필 데이터를 JSON으로 파싱
        Map<String, Object> profileMap = mapper.readValue(profileResponse.getBody(), Map.class);
        String displayName = (String) profileMap.get("displayName");

        // 새로운 사용자 생성
        Member member = new Member();
        String username = "testUser-" + UUID.randomUUID().toString();
        member.setUsername(username);
        member.setPassword("aaaa");
        member.setName(displayName);
        member.setBirth(LocalDate.of(1970, 1, 1));
        member.setGender(GenderType.MALE);
        memberRepository.save(member);  // 사용자 정보 저장
        log.info("success: Created new user {}", member);

        // 성공 후 /welcome으로 리다이렉트
        return "redirect:/welcome";
    }
}
