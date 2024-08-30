package com.fls.animecommunity.animesanctuary.service.interfaces;

import java.util.Map;

public interface OAuthService {
	String getAccessToken(String code);
    Map<String, Object> getUserProfile(String accessToken);
}
