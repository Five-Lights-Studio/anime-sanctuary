package com.fls.animecommunity.animesanctuary.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fls.animecommunity.animesanctuary.model.UpdateProfileRequest;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.MemberService;
import com.fls.animecommunity.animesanctuary.service.interfaces.OAuthService;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuthService oAuthService;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder, OAuthService oAuthService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.oAuthService = oAuthService;
    }

    @Override
    @Transactional
    public Member register(Member member) {
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }

    @Override
    public Member login(String username, String password) {
        Member member = memberRepository.findByUsername(username);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return member;
        }
        return null;
    }

    @Override
    public boolean deleteMember(Long id, String password) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public Member getProfile(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Member updateProfile(Long id, UpdateProfileRequest updateRequest) {
        Member existingMember = memberRepository.findById(id).orElse(null);
        if (existingMember == null) {
            return null;
        }

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            if (updateRequest.getCurrentPassword() == null ||
                !passwordEncoder.matches(updateRequest.getCurrentPassword(), existingMember.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            existingMember.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        existingMember.setUsername(updateRequest.getUsername() != null ? updateRequest.getUsername() : existingMember.getUsername());
        existingMember.setName(updateRequest.getName() != null ? updateRequest.getName() : existingMember.getName());
        existingMember.setBirth(updateRequest.getBirth() != null ? updateRequest.getBirth() : existingMember.getBirth());
        existingMember.setEmail(updateRequest.getEmail() != null ? updateRequest.getEmail() : existingMember.getEmail());
        existingMember.setGender(updateRequest.getGender() != null ? updateRequest.getGender() : existingMember.getGender());

        return memberRepository.save(existingMember);
    }
    
    @Override
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

    @Override
    public void updateProfileImage(Long userId, String filePath) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        member.setProfileImage(filePath);
        memberRepository.save(member);
    }

    @Override
    public void deleteProfileImage(Long userId) throws IOException {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

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

    @Override
    public String handleLineLogin(String code) {
        String accessToken = oAuthService.getAccessToken(code);
        Map<String, Object> userProfile = oAuthService.getUserProfile(accessToken);

        String lineUserId = (String) userProfile.get("userId");
        Member member = memberRepository.findByLineUserId(lineUserId);
        if (member == null) {
            member = new Member();
            member.setLineUserId(lineUserId);
            member.setUsername((String) userProfile.get("displayName"));
            member.setProfileImage((String) userProfile.get("pictureUrl"));
            memberRepository.save(member);
        }

        return accessToken;
    }
}
