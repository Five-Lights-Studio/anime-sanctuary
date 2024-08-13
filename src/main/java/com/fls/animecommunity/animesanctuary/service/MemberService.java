package com.fls.animecommunity.animesanctuary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fls.animecommunity.animesanctuary.model.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원 등록
    public Member register(Member member) {
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
    public boolean deleteMember(Long id, String password) {
    	Member member = memberRepository.findById(id).orElse(null);
    	if (member != null && passwordEncoder.matches(password, member.getPassword())) {
    		memberRepository.deleteById(id);
    		return true;  // 삭제 성공
    	}
    	return false;  // 비밀번호 불일치 또는 회원이 존재하지 않음
    }

    // 이메일로 사용자 찾기
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}