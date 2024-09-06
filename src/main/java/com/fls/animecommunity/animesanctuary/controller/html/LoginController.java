package com.fls.animecommunity.animesanctuary.controller.html;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
    
    @GetMapping("/welcome")
    public String welcome() {
        // 로그인한 사용자의 이름을 모델에 추가

        return "welcome"; // welcome.html 템플릿을 반환
    }
}
