package com.fls.animecommunity.animesanctuary.controller.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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
import com.fls.animecommunity.animesanctuary.service.impl.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@CrossOrigin(origins = {"http://localhost:9000/", "http://localhost:5501"})
@Slf4j
public class MemberController {

    private final MemberService memberService;
    
    //register 회원 등록
    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody MemberRegisterDto memberDto) {
        // Create a new Member object from the DTO
        Member member = new Member();
        member.setUsername(memberDto.getUsername());
        member.setPassword(memberDto.getPassword());
        member.setName(memberDto.getName());
        member.setEmail(memberDto.getEmail());
        member.setGender(GenderType.valueOf(memberDto.getGender().toUpperCase()));
        member.setBirth(memberDto.getBirth());

        // Register the member using the service
        Member registeredMember = memberService.register(member);
        
        return ResponseEntity.ok(registeredMember);
    }

    //login 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Member member = memberService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        log.info("member : {} ", member);
        if (member != null) {
            // 세션 생성
        	log.info("in if member : {} ", member);
            request.getSession().setAttribute("user", member);
            return ResponseEntity.ok(member);
        } else {
        	log.info("in else member : {} ", member);
            return ResponseEntity.status(401).body("Invalid username/email or password");
        }
    }
    
    //logout 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 세션 무효화
        request.getSession().invalidate();
        return ResponseEntity.ok("Logout successful");
    }
    
    //deleteMember 회원삭제
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
    
    //update Profile = member의 모든 요소(username, password, )를 수정할수있게 함
    //바뀐 부분이 있으면 update되는 방식, 이 메서드하나로 해결이 가능하다.분리시킬필요없음 
    //바꾸고 싶은것만 Body에 담아서 보내면 됨
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
    
    
    //Profile 프로필 관련 
    
    // getProfile 프로필 조회
    @GetMapping("/profile/{id}")
    public ResponseEntity<Member> getProfile(@PathVariable("id") Long id) {
        Member member = memberService.getProfile(id);
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
    
    //uploadProfileImage 프로필 이미지 업로드 하기
    @PostMapping("/{userId}/uploadProfileImage")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable("userId") Long userId, 
            @RequestParam("image") MultipartFile image) {
        try {
            // 저장할 파일 경로를 지정합니다 (경로 구분자를 명시적으로 사용하지 않음).
            String uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "profile-images", userId.toString()).toString();
            
            // 파일 저장 경로를 확인하고 디렉토리가 존재하지 않으면 생성합니다.
            File directory = new File(uploadDir);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + uploadDir);
            }

            // 파일 이름을 설정합니다.
            String originalFilename = image.getOriginalFilename();
            String filePath = Paths.get(uploadDir, UUID.randomUUID().toString() + "_" + originalFilename).toString();

            // 파일을 저장합니다.
            File destinationFile = new File(filePath);
            image.transferTo(destinationFile);

            // 업로드된 파일의 경로를 데이터베이스에 저장합니다.
            memberService.updateProfileImage(userId, filePath);

            // 저장된 파일의 경로 또는 URL을 반환합니다.
            return ResponseEntity.ok(filePath);
        } catch (IOException | IllegalStateException e) {
            return ResponseEntity.status(500).body("File I/O error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    //deleteProfileImage 프로필 이미지 삭제
    @DeleteMapping("/{userId}/deleteProfileImage")
    public ResponseEntity<String> deleteProfileImage(@PathVariable("userId") Long userId) {
        try {
            memberService.deleteProfileImage(userId);
            return ResponseEntity.ok("Profile image deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File I/O error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }


    // findByEmail 이메일로 사용자 찾기 
    @GetMapping("/findByEmail")
    public ResponseEntity<Member> findByEmail(@RequestParam String email) {
        Optional<Member> member = memberService.findByEmail(email);
        if (member.isPresent()) {
            return ResponseEntity.ok(member.get());
        } else {
            return ResponseEntity.status(404).build(); // Not Found
        }
    }
}