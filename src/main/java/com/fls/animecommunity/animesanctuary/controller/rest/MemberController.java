package com.fls.animecommunity.animesanctuary.controller.rest;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fls.animecommunity.animesanctuary.dto.LoginRequest;
import com.fls.animecommunity.animesanctuary.dto.MemberRegisterDto;
import com.fls.animecommunity.animesanctuary.model.UpdateProfileRequest;
import com.fls.animecommunity.animesanctuary.model.member.GenderType;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.service.impl.MemberService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = {"http://localhost:9000", "http://localhost:5501"})
public class MemberController {

	@Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository; // memberRepository 추가

    @Autowired
    private PasswordEncoder passwordEncoder; // passwordEncoder 추가

    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody MemberRegisterDto memberDto) {
        Member member = new Member();
        member.setUsername(memberDto.getUsername());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword())); // 패스워드 암호화
        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setGender(GenderType.valueOf(memberDto.getGender().toUpperCase()));
        member.setBirth(memberDto.getBirth());

        Member registeredMember = memberService.register(member);
        return ResponseEntity.ok(registeredMember);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String usernameOrEmail = loginRequest.getUsernameOrEmail();
        String password = loginRequest.getPassword();

        Optional<Member> memberOptional = memberRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();

            // 비밀번호가 일치하는지 확인
            if (passwordEncoder.matches(password, member.getPassword())) {
                request.getSession().setAttribute("user", member);
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 세션 무효화
        request.getSession().invalidate();
        return ResponseEntity.ok("Logout successful");
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMember(
        @PathVariable("id") Long id, 
        @RequestParam("username") String username, 
        @RequestParam("password") String password, 
        HttpServletRequest request
    ) {
        // 로그인 여부 확인
        Member loggedInMember = (Member) request.getSession().getAttribute("user");
        if (loggedInMember == null) {
            return ResponseEntity.status(403).build(); // 로그인하지 않은 경우, Forbidden
        }
        
        // 로그인한 사용자만 삭제 가능
        boolean isDeleted = memberService.deleteMember(id, password);
        if (isDeleted) {
            return ResponseEntity.ok().build(); // 회원 삭제 성공
        } else {
            return ResponseEntity.status(401).build(); // 비밀번호가 일치하지 않음, Unauthorized
        }
    }

    
    // 프로필 조회
    @GetMapping("/profile/{id}")
    public ResponseEntity<Member> getProfile(@PathVariable("id") Long id) {
        Member member = memberService.getProfile(id);
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
    
    // 프로필 수정
    @PostMapping("/updateProfile/{userId}")
    public ResponseEntity<?> updateProfile(
        @PathVariable("userId") Long userId,
        @RequestBody UpdateProfileRequest updateRequest
    ) {
        try {
            Member member = memberService.updateProfile(userId, updateRequest);
            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable("userId") Long userId, 
            @RequestParam("image") MultipartFile image) {
        try {
            // 저장할 파일 경로를 지정합니다 (절대 경로 사용).
            String uploadDir = System.getProperty("user.dir") + "/uploads/profile-images/" + userId;
            
            // 파일 저장 경로를 확인하고 디렉토리가 존재하지 않으면 생성합니다.
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();  // 여러 디렉토리를 한 번에 생성합니다.
                if (!created) {
                    throw new IOException("Failed to create directory: " + uploadDir);
                }
            }

            // 파일 이름을 설정합니다.
            String originalFilename = image.getOriginalFilename();
            String filePath = uploadDir + "/" + UUID.randomUUID().toString() + "_" + originalFilename; // 파일 이름 앞에 UUID 추가

            // 파일을 저장합니다.
            File destinationFile = new File(filePath);
            image.transferTo(destinationFile);

            // 업로드된 파일의 경로를 데이터베이스에 저장합니다.
            memberService.updateProfileImage(userId, filePath);

            // 저장된 파일의 경로 또는 URL을 반환합니다.
            return ResponseEntity.ok(filePath);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{userId}/deleteProfileImage")
    public ResponseEntity<?> deleteProfileImage(@PathVariable("userId") Long userId) {
        try {
            memberService.deleteProfileImage(userId);
            return ResponseEntity.ok("Profile image deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
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