package com.fls.animecommunity.animesanctuary.model.noteLike.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 좋아요를 추가하거나 취소할 때 필요한 정보: 게시물 ID(noteId)와 사용자의 ID(memberId)
 * @NotNull 붙인 이유 : client 요청을 한번더 검증하기 위함....라고 생각했으나 
 * 로그인 하지않은상태에도 좋아요 할수있도록 해야하므로 뺌
 */


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteLikeRequestDto {
	
//	@NotNull(message = "Note ID is required")
	private Long noteId; // 좋아요를 누를 게시물 ID

	private Long memberId; // 좋아요를 누른 사용자 ID
}
