package com.fls.animecommunity.animesanctuary.service.impl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.model.noteLike.NoteLike;
import com.fls.animecommunity.animesanctuary.model.noteLike.dto.NoteLikeRequestDto;
import com.fls.animecommunity.animesanctuary.model.noteLike.dto.NoteLikeResponseDto;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteLikeRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.NoteLikeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * 데이터베이스 저장소(예: JPA Repository)와의 상호작용을 구현
 * 
 * addLike : memberId가 null일 때의 처리 필요 (비로그인용 사용자)
 * getLikeStatus : 특정 사용자가 특정 게시물(noteId)에 대해 좋아요를 눌렀는지 여부를 확인하는 역할
 * getLikeCount()는 전체 좋아요 수를 반환하는 메서드
 * 
 * getLikeStatus는 @AuthenticationPrincipal , memberId 이슈로 일단 보류
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteLikeServiceImpl implements NoteLikeService {

	private final NoteLikeRepository noteLikeRepository;
	private final NoteRepository noteRepository;
	private final MemberRepository memberRepository;

	// addLike
	@Override
	public NoteLikeResponseDto addLike(@Valid NoteLikeRequestDto noteLikeRequestDto) {
		// 호출확인
		log.info("call NoteLikeService addLike()");

		// 1. noteLikeRequestDto 안의 noteId , memberId 가져오기
		Long noteId = noteLikeRequestDto.getNoteId();
		Long memberId = noteLikeRequestDto.getMemberId();

		// 2.console 출력
		log.info("noteId : {}", noteId);
		log.info("memberId : {}", memberId);

		// 3. Note 객체 가져오기
		Optional<Note> noteOptional = noteRepository.findById(noteId);
		
		//예외처리
		Note note = noteOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + noteId));
		
		
		// 4. memberId가 null인 경우 : 익명 사용자 처리 (분기점)
		if (memberId == null) {
			// 익명 사용자는 noteId만으로 중복 확인
			boolean isLikedByAnonymous = noteLikeRepository.existsByNoteIdAndMemberIdIsNull(noteId);
			//이미 존재한다 = true , 아니면 false
			
			Long likeCount = noteLikeRepository.countByNoteId(noteId); // 좋아요 수 계산
			
			if (!isLikedByAnonymous) {
				// 익명 사용자는 Member 없이 좋아요를 추가
				noteLikeRepository.save(new NoteLike(note, null));
				log.info("익명 사용자가 좋아요를 추가했습니다: noteId = {}", noteId);
				return new NoteLikeResponseDto(noteId, null, true, likeCount + 1, "익명 사용자의 좋아요가 추가되었습니다.");
			} else {
				log.info("익명 사용자가 이미 좋아요를 눌렀습니다: noteId = {}", noteId);
				return new NoteLikeResponseDto(noteId, null, true, likeCount, "익명 사용자가 이미 좋아요를 눌렀습니다.");
			}
		} else {
			// 5. 로그인한 사용자 ,memberId가 있을 경우 처리
			Optional<Member> memberOptional = memberRepository.findById(memberId);
			Member member = memberOptional
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: " + memberId));

			// 6.이미 좋아요를 눌렀는지 확인 (중복 방지)
			boolean isLikedByMember = noteLikeRepository.existsByNoteIdAndMemberId(noteId, memberId);
			Long likeCount = noteLikeRepository.countByNoteId(noteId); // 좋아요 수 계산
			
			if (!isLikedByMember) {
				// 좋아요 추가
				noteLikeRepository.save(new NoteLike(note, member));
				log.info("좋아요 추가: noteId = {}, memberId = {}", noteId, memberId);
				
				//응답 message 생성
				String message = "noteId : "+ noteId +" 에 "+ "memberId : " + memberId + "의 좋아요가 추가되었습니다."; 
				
				return new NoteLikeResponseDto(noteId, memberId, true, likeCount + 1, message);
			} else {
				log.info("이미 좋아요를 누른 상태입니다: noteId = {}, memberId = {}", noteId, memberId);
				return new NoteLikeResponseDto(noteId, memberId, true, likeCount, "이미 좋아요를 누른 상태입니다.");
			}
		}
	}

	@Override
	public NoteLikeResponseDto removeLike(@Valid NoteLikeRequestDto noteLikeRequestDto) {
	    // 호출 확인
	    log.info("call NoteLikeService removeLike()");

	    // 1. noteLikeRequestDto 안의 noteId, memberId 가져오기
	    Long noteId = noteLikeRequestDto.getNoteId();
	    Long memberId = noteLikeRequestDto.getMemberId();

	    // 2. 콘솔 출력
	    log.info("noteId : {}", noteId);
	    log.info("memberId : {}", memberId);

	    // 3. Note 객체 가져오기
	    Optional<Note> noteOptional = noteRepository.findById(noteId);
	    Note note = noteOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found with id: " + noteId));

	    if (memberId == null) {
	        // 4. memberId가 null인 경우 익명 사용자 처리
	        boolean isLikedByAnonymous = noteLikeRepository.existsByNoteIdAndMemberIdIsNull(noteId);

	        if (isLikedByAnonymous) {
	            // 익명 사용자의 좋아요 삭제
	            NoteLike noteLike = noteLikeRepository.findByNoteIdAndMemberIdIsNull(noteId)
	                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found for noteId: " + noteId));
	            noteLikeRepository.delete(noteLike);
	            log.info("익명 사용자의 좋아요가 삭제되었습니다: noteId = {}", noteId);

	            // 좋아요 수 계산
	            Long likeCount = noteLikeRepository.countByNoteId(noteId);
	            return new NoteLikeResponseDto(noteId, null, false, likeCount, "익명 사용자의 좋아요가 삭제되었습니다.");
	        } else {
	            log.info("익명 사용자가 좋아요를 누르지 않았습니다: noteId = {}", noteId);
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"익명 사용자가 이 게시물에 좋아요를 누르지 않았습니다: noteId = " + noteId);
	        }
	    } else {
	        // 5. 로그인한 사용자 처리
	        Optional<Member> memberOptional = memberRepository.findById(memberId);
	        Member member = memberOptional
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Member not found with id: " + memberId));

	        boolean isLikedByMember = noteLikeRepository.existsByNoteIdAndMemberId(noteId, memberId);

	        if (isLikedByMember) {
	            // 사용자의 좋아요 삭제
	            NoteLike noteLike = noteLikeRepository.findByNoteIdAndMemberId(noteId, memberId)
	                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
	                            "Like not found for noteId: " + noteId + " and memberId: " + memberId));
	            noteLikeRepository.delete(noteLike);
	            log.info("좋아요가 삭제되었습니다: noteId = {}, memberId = {}", noteId, memberId);

	            // 좋아요 수 계산
	            Long likeCount = noteLikeRepository.countByNoteId(noteId);
	            
	          //응답 message 생성
				String message = "noteId : "+ noteId +" 에 "+ "memberId : " + memberId + "의 좋아요가 삭제되었습니다.";
	            
	            return new NoteLikeResponseDto(noteId, memberId, false, likeCount, message);
	        } else {
	            log.info("해당 사용자가 좋아요를 누르지 않았습니다: noteId = {}, memberId = {}", noteId, memberId);
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"이 사용자가 이 게시물에 좋아요를 누르지 않았습니다: noteId = " + noteId + ", memberId = " + memberId);
	        }
	    }
	}

	@Override
	public NoteLikeResponseDto getLikeStatus(Long noteId, Long memberId) {
	    // 호출 확인
	    log.info("call NoteLikeService getLikeStatus()");

	    // 1. Note 객체 확인
	    Optional<Note> noteOptional = noteRepository.findById(noteId);
	    Note note = noteOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Note not found with id: " + noteId));

	    // 2. 좋아요 여부 확인
	    boolean isLiked = noteLikeRepository.existsByNoteIdAndMemberId(noteId, memberId);
	    
	    // 3. 좋아요 수 확인
	    Long likeCount = noteLikeRepository.countByNoteId(noteId);

	    // 4. 응답 생성
	    NoteLikeResponseDto noteLikeResponseDto = new NoteLikeResponseDto();
	    noteLikeResponseDto.setIsLiked(isLiked);
	    noteLikeResponseDto.setNoteId(noteId);
	    noteLikeResponseDto.setMemberId(memberId);
	    noteLikeResponseDto.setLikeCount(likeCount);
	    noteLikeResponseDto.setMessage(isLiked ? "사용자가 이 게시물에 좋아요를 눌렀습니다." : "사용자가 이 게시물에 좋아요를 누르지 않았습니다.");

	    return noteLikeResponseDto;
	}


	@Override
	public NoteLikeResponseDto getLikeCount(Long noteId) {
	    // 호출 확인
	    log.info("call NoteLikeService getLikeCount()");

	    // 1. noteId로 게시물 확인
	    Optional<Note> noteOptional = noteRepository.findById(noteId);
	    Note note = noteOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Note not found with id: " + noteId));

	    // 2. 좋아요 수 확인
	    Long likeCount = noteLikeRepository.countByNoteId(noteId);
	    log.info("Like count for noteId {} : {}", noteId, likeCount);

	    // 3. 좋아요 수 반환
	    String message = likeCount > 0 ? "좋아요 수 조회 성공" : "좋아요가 없습니다.";
	    return new NoteLikeResponseDto(noteId, likeCount, message);
	}
}
