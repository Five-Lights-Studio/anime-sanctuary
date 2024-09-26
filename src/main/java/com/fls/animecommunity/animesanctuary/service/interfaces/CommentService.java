package com.fls.animecommunity.animesanctuary.service.interfaces;

import java.util.List;

import com.fls.animecommunity.animesanctuary.dto.CommentRequestDto;
import com.fls.animecommunity.animesanctuary.dto.CommentResponseDto;

public interface CommentService {
    CommentResponseDto addComment(Long noteId, CommentRequestDto requestDto);
    List<CommentResponseDto> getCommentsByNoteId(Long noteId);
    void updateComment(Long commentId, Long memberId, CommentRequestDto requestDto);
    void deleteComment(Long commentId, Long memberId);
}
