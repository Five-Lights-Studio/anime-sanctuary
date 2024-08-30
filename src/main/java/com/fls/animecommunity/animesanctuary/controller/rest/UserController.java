package com.fls.animecommunity.animesanctuary.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
	
	//user-info
	@GetMapping("/user-info")
    public String getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName(); // 사용자의 이름 (주로 사용자 ID)
        Object principal = authentication.getPrincipal(); // 주로 UserDetails 객체

        return "Authenticated user: " + username;
    }
	
	
}
