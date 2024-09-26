package com.fls.animecommunity.animesanctuary.dto;

import lombok.Data;

@Data
public class CommentRequestDto {
    private String content;  // 'content'로 수정
    private Long noteId;
    private Long memberId;
}
