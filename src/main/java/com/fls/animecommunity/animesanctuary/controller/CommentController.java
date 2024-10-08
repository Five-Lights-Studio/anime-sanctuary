package com.fls.animecommunity.animesanctuary.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.model.comment.Comment;
import com.fls.animecommunity.animesanctuary.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestParam Long noteId,
                                              @RequestParam String content,
                                              @RequestParam(required = false) Long parentCommentId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        // 로그인된 사용자의 username을 통해 댓글 추가
        Comment comment = commentService.addComment(userDetails.getUsername(), noteId, content, parentCommentId);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Comment>> getCommentsByNoteId(@PathVariable Long noteId) {
        return ResponseEntity.ok(commentService.getCommentsByNoteId(noteId));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Comment>> getCommentsByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(commentService.getCommentsByMemberId(memberId));
    }

    @GetMapping("/replies/{commentId}")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getReplies(commentId));
    }
}
