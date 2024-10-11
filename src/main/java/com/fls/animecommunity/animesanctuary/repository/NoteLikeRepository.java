package com.fls.animecommunity.animesanctuary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fls.animecommunity.animesanctuary.model.noteLike.NoteLike;

public interface NoteLikeRepository extends JpaRepository<NoteLike, Long>{
	
	//noteId memberId 로 이미 좋아요를 눌렀는지 확인하는 쿼리메서드
	boolean existsByNoteIdAndMemberId(Long noteId, Long memberId);
	
	//비로그인 사용자용 : 익명 사용자가 이미 좋아요를 눌렀는지 확인하기 위해
	//해당 noteId, MemberIdIsNull인 NoteLike가 있는지 없는지 확인
	boolean existsByNoteIdAndMemberIdIsNull(Long noteId);
	
	//noteId , memberId 로 해당 NoteLike를 찾아오는
	Optional<NoteLike> findByNoteIdAndMemberId(Long noteId, Long memberId);
	
	//noteId로만 찾기 , memberId는 null인
	Optional<NoteLike> findByNoteIdAndMemberIdIsNull(Long noteId);
	
	// getLikeCount
	Long countByNoteId(Long noteId);

}
