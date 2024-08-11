package com.fls.animecommunity.animesanctuary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.model.Member;
import com.fls.animecommunity.animesanctuary.service.MemberService;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody Member member) {
        Member registeredMember = memberService.register(member);
        return ResponseEntity.ok(registeredMember);
    }

    @PostMapping("/login")
    public ResponseEntity<Member> login(@RequestParam String username, @RequestParam String password) {
        Member member = memberService.login(username, password);
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
    }

    // 이메일로 사용자 찾기 (추가 기능)
    @GetMapping("/findByEmail")
    public ResponseEntity<Member> findByEmail(@RequestParam String email) {
        Member member = memberService.findByEmail(email);
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.status(404).build(); // Not Found
        }
    }
}