package com.fls.animecommunity.animesanctuary.controller.html;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * Home ,main , index page
 */

@Slf4j
@Controller
public class HomeController {
	
	//Home ,main , index page
	@GetMapping("/")
	public String home() {
		log.info("home() 실행");
		return "index";
	}	
}
