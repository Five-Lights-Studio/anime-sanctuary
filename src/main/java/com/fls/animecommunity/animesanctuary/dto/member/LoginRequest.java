package com.fls.animecommunity.animesanctuary.dto.member;

import lombok.Data;

@Data
public class LoginRequest {
	private String usernameOrEmail;
    private String password;
}
