package com.fls.animecommunity.animesanctuary.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.dto.NoteLikeDto.NoteLikeRequestDto;
import com.fls.animecommunity.animesanctuary.dto.NoteLikeDto.NoteLikeResponseDto;
import com.fls.animecommunity.animesanctuary.service.interfaces.NoteLikeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
좋아요 기능 api endpoint :

좋아요 추가: 
POST api/notes/{noteId}/noteLike

좋아요 취소: 
DELETE api/notes/{noteId}/noteLike

좋아요 상태 조회: 현재 로그인된 사용자가 이 게시물에 좋아요를 눌렀는가? 에 대한 정보
GET api/notes/{noteId}/noteLike

좋아요 수 조회: 그냥 수만 
GET api/notes/{noteId}/noteLike/count

*상태를 조회하는 거랑 , 수를 조회하는거   분리함
*리턴타입을 boolean이나 Long으로 바꾸지않고 일관성을 유지하도록함

 * Body에 좋아요에 대한 정보를 담아 요청을 보낸다. 
 * 필요한 정보 noteid / memberid
 * 
 * method name : 
 * addLike() / removeLike()
 * 
 * 
 * //getLikeStatus Get에 @RequestBody를 쓰는것은 일반적이지 않음.
 * 현재는 @RequestParam으로 했음 보안적 문제
 * @AuthenticationPrincipal 도입고려
 * 
 * 
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/notes/{noteId}/noteLike")
public class NoteLikeController {

	private final NoteLikeService noteLikeService;
	
	//addLike
	@PostMapping
	public ResponseEntity<NoteLikeResponseDto> addLike(@PathVariable("noteId") Long noteId,
			@RequestBody @Valid NoteLikeRequestDto noteLikeRequestDto) {
		
		//호출확인
		log.info("call addLike()");

		// RequestDto에 noteId를 설정
		noteLikeRequestDto.setNoteId(noteId);

		// 그다음 noteLikeService의 addLike호출 하여 noteLikeResponseDto를 생성
		NoteLikeResponseDto noteLikeResponseDto = noteLikeService.addLike(noteLikeRequestDto);

		log.info("noteLikeResponseDto : {} ", noteLikeResponseDto);

		// 그후 client에게 noteLikeResponseDto를 리턴
		return ResponseEntity.ok(noteLikeResponseDto);
	}
	
	//removeLike
	@DeleteMapping
	public ResponseEntity<NoteLikeResponseDto> removeLike(@PathVariable("noteId") Long noteId,
			@RequestBody @Valid NoteLikeRequestDto noteLikeRequestDto) {

		log.info("call removeLike()");

		// RequestDto에 noteId를 설정
		noteLikeRequestDto.setNoteId(noteId);

		// 그다음 noteLikeService의 removeLike호출 하여 noteLikeResponseDto를 생성
		NoteLikeResponseDto noteLikeResponseDto = noteLikeService.removeLike(noteLikeRequestDto);

		log.info("noteLikeResponseDto : {} ", noteLikeResponseDto);
		
		// 그후 client에게 noteLikeResponseDto를 리턴
		return ResponseEntity.ok(noteLikeResponseDto);
	}
	
	//getLikeStatus
	@GetMapping
	public ResponseEntity<NoteLikeResponseDto> getLikeStatus(@PathVariable("noteId") Long noteId,
	                                                         @RequestParam("memberId") Long memberId) {
	    log.info("call getLikeStatus() for noteId: {} and memberId: {}", noteId, memberId);

	    // NoteLikeService를 통해 좋아요 상태 확인
	    NoteLikeResponseDto noteLikeResponseDto = noteLikeService.getLikeStatus(noteId, memberId);
	    
	    log.info("noteLikeResponseDto: {}", noteLikeResponseDto);
	    
	    return ResponseEntity.ok(noteLikeResponseDto);
	}

	
	//getLikeCount
	@GetMapping("/count")
	public ResponseEntity<NoteLikeResponseDto> getLikeCount(@PathVariable("noteId") Long noteId) {

		log.info("call getLikeCount()");

		// 그다음 noteLikeService의 getLikeCount호출 하여 noteLikeResponseDto를 생성
		NoteLikeResponseDto noteLikeResponseDto = noteLikeService.getLikeCount(noteId);
		
		log.info("noteLikeResponseDto : {} ", noteLikeResponseDto);
		
		return ResponseEntity.ok(noteLikeResponseDto);
	}

}
