package com.fls.animecommunity.animesanctuary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.model.Member;
import com.fls.animecommunity.animesanctuary.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (member != null) {
            // 세션 생성
            request.getSession().setAttribute("user", member);
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 세션 무효화
        request.getSession().invalidate();
        return ResponseEntity.ok("Logout successful");
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable("id") Long id, @RequestParam("password") String password) {
        boolean isDeleted = memberService.deleteMember(id, password);
        if (isDeleted) {
            return ResponseEntity.ok().build(); // 회원 삭제 성공
        } else {
            return ResponseEntity.status(401).build(); // 비밀번호가 일치하지 않음, Unauthorized
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