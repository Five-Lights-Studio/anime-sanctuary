package com.fls.animecommunity.animesanctuary.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fls.animecommunity.animesanctuary.model.UpdateProfileRequest;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, NoteRepository noteRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.noteRepository = noteRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    //register
    @Transactional
    public Member register(Member member) {
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (member.getPassword() == null || member.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        return memberRepository.save(member);
    }
    
    //login
    public Member login(String usernameOrEmail, String password) {
        Optional<Member> member = memberRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        return member.filter(m -> passwordEncoder.matches(password, m.getPassword()))
                     .orElse(null);
    }

    @Transactional
    public boolean deleteMember(Long id, String password) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent() && passwordEncoder.matches(password, member.get().getPassword())) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Member> findByEmail(String email) {
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

        Optional.ofNullable(updateRequest.getUsername()).ifPresent(existingMember::setUsername);
        Optional.ofNullable(updateRequest.getName()).ifPresent(existingMember::setName);
        Optional.ofNullable(updateRequest.getBirth()).ifPresent(existingMember::setBirth);
        Optional.ofNullable(updateRequest.getEmail()).ifPresent(existingMember::setEmail);
        Optional.ofNullable(updateRequest.getGender()).ifPresent(existingMember::setGender);

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            if (updateRequest.getCurrentPassword() == null ||
                !passwordEncoder.matches(updateRequest.getCurrentPassword(), existingMember.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            existingMember.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

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
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }

            member.setProfileImage(null);
            memberRepository.save(member);
        }
    }

    @Transactional
    public Member registerOrLoginWithSocial(String name, String email, String provider, String providerId) {
        Member member = memberRepository.findByEmail(email)
                .orElse(new Member());
        member.setEmail(email);
        member.setName(name);
        member.setProvider(provider);
        member.setProviderId(providerId);
        return memberRepository.save(member);
    }

    @Transactional
    public void saveOrUpdateMember(OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        Member member = memberRepository.findByEmail(email)
                .orElse(new Member());
        member.setEmail(email);
        member.setName(oidcUser.getFullName());
        member.setProvider("google");
        member.setProviderId(oidcUser.getAttribute("sub"));
        memberRepository.save(member);
    }

    @Transactional
    public boolean saveNoteForUser(Long memberId, Long noteId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));

        if (!member.getSavedNotes().contains(note)) {
            member.getSavedNotes().add(note);
            memberRepository.save(member);
            return true;
        }
        return false;
    }
}
