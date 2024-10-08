package com.fls.animecommunity.animesanctuary.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fls.animecommunity.animesanctuary.model.comment.Comment;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.repository.CommentRepository;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;
    private final NotificationService notificationService;

    @Transactional
    public Comment addComment(String username, Long noteId, String content, Long parentCommentId) {
        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));

        Comment comment = new Comment();
        comment.setMember(member);
        comment.setNote(note);
        comment.setContent(content);

        if (parentCommentId != null) {
            Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

            // 중첩된 댓글이 있는지 확인 (Check any duplicated comment exist)
            if (parentComment.getParentComment() != null) {
                throw new IllegalArgumentException("Nested replies are not allowed");
            }

            comment.setParentComment(parentComment);
            
            // 대댓글이 달린 경우 원 댓글 작성자에게 알림 전송 (Notify to parentComment author when detected reply)
            notificationService.sendNotification(parentComment.getMember(), comment, "당신의 댓글에 답글이 달렸습니다.");
        }

        return commentRepository.save(comment);
    }


    public List<Comment> getCommentsByNoteId(Long noteId) {
        return commentRepository.findByNoteId(noteId);
    }

    public List<Comment> getCommentsByMemberId(Long memberId) {
        return commentRepository.findByMemberId(memberId);
    }

    public List<Comment> getReplies(Long parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId);
    }
}
