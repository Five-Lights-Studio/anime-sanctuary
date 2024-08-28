package com.fls.animecommunity.animesanctuary.controller.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.service.impl.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LineLoginController {
	
	//DI
	private final MemberService memberService;
	
	//로그인 후 code와 state 값을 받아옴 , Callback URL
	@GetMapping("/callback")
	public String getMethodName(@RequestParam("code") String code, 
            					@RequestParam("state") String state) {
		
		// code와 state 값을 사용해 처리하는 로직을 추가합니다.
	    // 예를 들어, code를 사용해 LINE 서버에 액세스 토큰을 요청할 수 있습니다.
		return "redirect:/home";
	}
	
}
