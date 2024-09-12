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

import com.fls.animecommunity.animesanctuary.dto.profileDto.UpdateProfileRequest;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.member.Role;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Member register(Member member) {
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (member.getPassword() == null || member.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        System.out.println("Password before encoding: " + member.getPassword());

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    public Member login(String usernameOrEmail, String password) {
        Member member = memberRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return member;
        }
        return null;
    }

    @Transactional
    public boolean deleteMember(Long id, String password) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member getProfile(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    @Transactional
    public Member updateProfile(Long id, UpdateProfileRequest updateRequest) {
        Member existingMember = memberRepository.findById(id).orElse(null);
        if (existingMember == null) {
            return null;
        }
        
        //새로운 password를 설정하는 로직
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
        	// 현재 비밀번호가 null이거나, 현재 비밀번호가 기존 비밀번호와 일치하지 않는 경우 에러를 발생시킨다.
            if (updateRequest.getCurrentPassword() == null ||
                !passwordEncoder.matches(updateRequest.getCurrentPassword(), existingMember.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            // 현재 비밀번호가 맞으면 새로운 비밀번호로 업데이트
            existingMember.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        //username(=id) , 이름 등 가져온 것이 null이 아니다 그러면 수정하고 , null이 라면 기존것 그대로 
        existingMember.setUsername(updateRequest.getUsername() != null ? updateRequest.getUsername() : existingMember.getUsername());
        existingMember.setName(updateRequest.getName() != null ? updateRequest.getName() : existingMember.getName());
        existingMember.setBirth(updateRequest.getBirth() != null ? updateRequest.getBirth() : existingMember.getBirth());
        existingMember.setEmail(updateRequest.getEmail() != null ? updateRequest.getEmail() : existingMember.getEmail());
        existingMember.setGender(updateRequest.getGender() != null ? updateRequest.getGender() : existingMember.getGender());

        return memberRepository.save(existingMember);
    }

    public String uploadProfileImage(Long userId, MultipartFile image) throws IOException {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        String uploadDir = "uploads/profile-images/";
        Files.createDirectories(Paths.get(uploadDir));

        String originalFilename = image.getOriginalFilename();
        String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        String imagePath = uploadDir + newFilename;

        File imageFile = new File(imagePath);
        image.transferTo(imageFile);

        member.setProfileImage(imagePath);
        memberRepository.save(member);

        return imagePath;
    }

    public void updateProfileImage(Long userId, String filePath) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        member.setProfileImage(filePath);
        memberRepository.save(member);
    }

    public void deleteProfileImage(Long userId) throws IOException {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        String filePath = member.getProfileImage();
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new IOException("Failed to delete file: " + filePath);
                }
            }

            member.setProfileImage(null);
            memberRepository.save(member);
        }
    }

    @Transactional
    public Member registerOrLoginWithSocial(String name, String email, String provider, String providerId) {
        Member existingMember = memberRepository.findByEmail(email);

        if (existingMember != null) {
            // 기존 회원인 경우
            return existingMember;
        } else {
            // 신규 회원인 경우
            Member newMember = new Member(name, email, provider, providerId, Role.USER);
            // 패스워드를 비워두거나 기본값으로 설정
            newMember.setPassword("");  // 패스워드를 빈 문자열로 설정
            return memberRepository.save(newMember);
        }
    }


}
