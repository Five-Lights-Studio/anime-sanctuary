package com.fls.animecommunity.animesanctuary.dto.noteLike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * NoteLikeResponseDto에 필요한 정보 : 
 * 		어떤 게시물인지 noteId
 * 		좋아요 여부 isLiked
 * 		좋아요 수 likeCount 
 * 		성공여부 message
 * 
 * 		좋아요를 누른 사용자의 정보 memberId는 개인정보보호 때문에 노출되지 않도록 처리하지만, 
 * 		지금은 DTO를 분리하지 않았으므로 필요 시 null로 처리할 수 있음.
 */

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteLikeResponseDto {
	
//	@NotNull(message = "Note ID is required")
	private Long noteId; // 해당 게시물 ID
	
	private Long memberId; // 좋아요를 누른 사용자 ID
	
	private Boolean isLiked; // 사용자가 이미 그 게시물에 좋아요를 눌렀는지 여부
	
	private Long likeCount;  // 게시물의 총 좋아요 수
	
	private String message;  // 사용자에게 반환할 메시지
	
	// 게시물의 ID와 좋아요 수, 메시지를 입력받는 생성자
	public NoteLikeResponseDto(Long noteId, Long likeCount, String message) {
		this.noteId = noteId;
		this.likeCount = likeCount;
		this.message = message;
	}
}
