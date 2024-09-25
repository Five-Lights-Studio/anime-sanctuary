package com.fls.animecommunity.animesanctuary.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "Login page";
    }

    @GetMapping("/home")
    public String home() {
        return "Home page";
    }
}
