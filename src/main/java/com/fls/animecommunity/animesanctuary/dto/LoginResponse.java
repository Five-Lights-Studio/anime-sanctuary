package com.fls.animecommunity.animesanctuary.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long memberId;
    private String message;

    public LoginResponse(Long memberId, String message) {
        this.memberId = memberId;
        this.message = message;
    }

}
