package com.fls.animecommunity.animesanctuary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_shouldSaveMember_whenValidMemberIsGiven() {
        Member member = new Member();
        member.setUsername("testUser");
        member.setPassword("testPassword");

        when(memberRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member registeredMember = memberService.register(member);

        assertEquals(member.getUsername(), registeredMember.getUsername());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void login_shouldReturnMember_whenValidCredentialsAreGiven() {
        Member member = new Member();
        member.setUsername("testUser");
        member.setPassword("encodedPassword");

        when(memberRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Member loggedInMember = memberService.login("testUser", "password");

        assertNotNull(loggedInMember);
    }
    
    @Test
    void login_shouldReturnNull_whenInvalidCredentialsAreGiven() {
        when(memberRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Member loggedInMember = memberService.login("testUser", "wrongPassword");

        assertNull(loggedInMember);
    }
}
