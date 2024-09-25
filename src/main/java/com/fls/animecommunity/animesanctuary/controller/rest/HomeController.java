package com.fls.animecommunity.animesanctuary.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HomeController {

    // Home, main, index page
    @GetMapping("/")
    public String home() {
        // 단순히 메시지를 반환
        return "Welcome to Anime Sanctuary!";
    }
}
