package com.fls.animecommunity.animesanctuary.service.interfaces;

import com.fls.animecommunity.animesanctuary.model.UpdateProfileRequest;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MemberService {
    Member register(Member member);
    Member login(String username, String password);
    boolean deleteMember(Long id, String password);
    Member findByEmail(String email);
    Member getProfile(Long id);
    Member updateProfile(Long id, UpdateProfileRequest updateRequest);
    String uploadProfileImage(Long userId, MultipartFile image) throws IOException;
    void updateProfileImage(Long userId, String filePath);
    void deleteProfileImage(Long userId) throws IOException;
    String handleLineLogin(String code);
}
