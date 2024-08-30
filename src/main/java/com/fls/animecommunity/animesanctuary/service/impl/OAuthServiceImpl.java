package com.fls.animecommunity.animesanctuary.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
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
        String clientId = "YOUR_CLIENT_ID";
        String clientSecret = "YOUR_CLIENT_SECRET";
        String redirectUri = "http://localhost:9000/callback";
        String tokenUrl = "https://api.line.me/oauth2/v2.1/token";

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("grant_type", "authorization_code");
        requestParams.put("code", code);
        requestParams.put("redirect_uri", redirectUri);
        requestParams.put("client_id", clientId);
        requestParams.put("client_secret", clientSecret);

        try {
            Map<String, String> response = restTemplate.postForObject(tokenUrl, requestParams, Map.class);
            return response.get("access_token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get access token from Line", e);
        }
    }

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
