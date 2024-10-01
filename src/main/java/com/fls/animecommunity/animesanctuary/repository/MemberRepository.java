package com.fls.animecommunity.animesanctuary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fls.animecommunity.animesanctuary.model.member.Member;

// Member entity 관리하기 위한 repository!
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUsername(String username);
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsernameOrEmail(String username, String email);
	boolean existsByEmail(String email);
}