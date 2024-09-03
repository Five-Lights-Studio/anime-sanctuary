package com.fls.animecommunity.animesanctuary.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fls.animecommunity.animesanctuary.service.interfaces.OAuthService;


@Service
public class OAuthServiceImpl implements OAuthService {

    private final RestTemplate restTemplate;

    public OAuthServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getAccessToken(String code) {
        String clientId = "2006160018";
        String clientSecret = "4a571572cdc1b33ed63ef4daf2988b3e";
        String redirectUri = "http://localhost:9000/auth/callback";
        String tokenUrl = "https://api.line.me/oauth2/v2.1/token";
        
     // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        //Content-Type application/x-www-form-urlencoded
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
     // 요청 바디 설정
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code); // 전달받은 code를 사용
        map.add("redirect_uri", "http://localhost:9000/auth/callback");
        map.add("client_id", "2006160018");
        map.add("client_secret", "4a571572cdc1b33ed63ef4daf2988b3e");
        //optional
//        map.add("code_verifier", "wJKN8qz5t8SSI9lMFhBB6qwNkQBkuPZoCxzRhwLRUo1");
        
     // 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        
        try {
        	// 요청을 보내고 응답을 Map으로 받기
            Map<String, String> response = restTemplate.postForObject(tokenUrl, request, Map.class);
         // access_token이 응답에 포함되었는지 확인
            return response != null ? response.get("access_token") : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token from Line", e);
        }
    }
    
    //프로필요청
    @Override
    public Map<String, Object> getUserProfile(String accessToken) {
        String profileUrl = "https://api.line.me/v2/profile";

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);

            Map<String, Object> response = restTemplate.getForObject(profileUrl, Map.class, headers);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user profile from Line", e);
        }
    }
}
