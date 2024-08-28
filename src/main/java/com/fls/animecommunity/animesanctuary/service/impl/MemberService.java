package com.fls.animecommunity.animesanctuary.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fls.animecommunity.animesanctuary.model.UpdateProfileRequest;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원 등록 (트랜잭션 적용)
    @Transactional
    public Member register(Member member) {
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 비밀번호 암호화 후 저장
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    // 로그인
    public Member login(String username, String password) {
        Member member = memberRepository.findByUsername(username);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return member;
        }
        return null;
    }

    // 회원 삭제 메서드, 비밀번호 확인 포함
    @Transactional
    public boolean deleteMember(Long id, String password) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 이메일로 사용자 찾기
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // 프로필 조회
    public Member getProfile(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    // 프로필 수정
    @Transactional
    public Member updateProfile(Long id, UpdateProfileRequest updateRequest) {
        // 사용자 찾기
        Member existingMember = memberRepository.findById(id).orElse(null);
        if (existingMember == null) {
            return null;  // Member not found
        }

        // 비밀번호 변경 시 확인 절차
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            if (updateRequest.getCurrentPassword() == null ||
                !passwordEncoder.matches(updateRequest.getCurrentPassword(), existingMember.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            existingMember.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        // 필드 업데이트
        existingMember.setUsername(updateRequest.getUsername() != null ? updateRequest.getUsername() : existingMember.getUsername());
        existingMember.setName(updateRequest.getName() != null ? updateRequest.getName() : existingMember.getName());
        existingMember.setBirth(updateRequest.getBirth() != null ? updateRequest.getBirth() : existingMember.getBirth());
        existingMember.setEmail(updateRequest.getEmail() != null ? updateRequest.getEmail() : existingMember.getEmail());
        existingMember.setGender(updateRequest.getGender() != null ? updateRequest.getGender() : existingMember.getGender());

        // 업데이트된 회원 저장
        return memberRepository.save(existingMember);
    }
    
    public String uploadProfileImage(Long userId, MultipartFile image) throws IOException {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 이미지 저장 경로 설정 (로컬 서버의 경로 또는 S3와 같은 외부 저장소를 사용)
        String uploadDir = "uploads/profile-images/";
        Files.createDirectories(Paths.get(uploadDir)); // 경로가 없으면 생성

        // 이미지 파일명 생성
        String originalFilename = image.getOriginalFilename();
        String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        String imagePath = uploadDir + newFilename;

        // 이미지 파일 저장
        File imageFile = new File(imagePath);
        image.transferTo(imageFile);

        // Member 엔티티에 이미지 파일명 저장
        member.setProfileImage(imagePath);
        memberRepository.save(member);

        return imagePath; // 이미지 파일 경로 또는 URL 반환
    }
    
    public void updateProfileImage(Long userId, String filePath) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        member.setProfileImage(filePath); // profile_image 필드에 경로 설정
        memberRepository.save(member); // 변경 사항 저장
    }

    public void deleteProfileImage(Long userId) throws IOException {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String filePath = member.getProfileImage();
        if (filePath != null && !filePath.isEmpty()) {
            // 파일 시스템에서 이미지 삭제
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new IOException("Failed to delete file: " + filePath);
                }
            }

            // 데이터베이스에서 이미지 경로 삭제
            member.setProfileImage(null);
            memberRepository.save(member);
        }
    }

	public String getAccessToken(String code) {
		// TODO Auto-generated method stub
		return null;
	}

}
