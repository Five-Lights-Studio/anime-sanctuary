package com.fls.animecommunity.animesanctuary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

import com.fls.animecommunity.animesanctuary.model.post.dto.PostResponseDto;
import com.fls.animecommunity.animesanctuary.service.PostService;


@Slf4j
@Controller
@RequestMapping("board")
@RequiredArgsConstructor
public class PageController {
	
	private final PostService postService;
	
	
	//list
	@GetMapping("list")
	public String list() {
		return "list";
	}
	
	//read
	@GetMapping("read/{postId}")
	public String getPost(@PathVariable("postId") Long id, Model model) {
	    PostResponseDto post = postService.getPost(id);
	    model.addAttribute("post", post);
	    return "read"; // "read.html" 템플릿 반환
	}

	
	//write
	@GetMapping("write")
	public String write() {
		return "write";
	}
	
	//update
	@GetMapping("update/{postId}")
	public String update(@PathVariable("postId") Long id) {
		return "update";
	}
	
	
	
}