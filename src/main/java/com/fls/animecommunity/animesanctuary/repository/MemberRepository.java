package com.fls.animecommunity.animesanctuary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fls.animecommunity.animesanctuary.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{

	Member findByUsername(String username);
    Member findByEmail(String email);
}