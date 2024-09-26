package com.fls.animecommunity.animesanctuary.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fls.animecommunity.animesanctuary.dto.CommentRequestDto;
import com.fls.animecommunity.animesanctuary.dto.CommentResponseDto;
import com.fls.animecommunity.animesanctuary.exception.ResourceNotFoundException;
import com.fls.animecommunity.animesanctuary.model.comment.Comment;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.repository.CommentRepository;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NoteRepository noteRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public CommentResponseDto addComment(Long noteId, CommentRequestDto requestDto) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Comment comment = new Comment();
        comment.setContent(requestDto.getContent()); // 수정된 필드 이름 사용
        comment.setNote(note);  // 댓글을 특정 노트에 연결
        comment.setMember(member);  // 댓글 작성자 설정

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment saved with id: {}", savedComment.getId());

        return new CommentResponseDto(savedComment);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByNoteId(Long noteId) {
        return commentRepository.findByNoteId(noteId)
                .stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, Long memberId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("You are not the author of this comment.");
        }

        comment.updateContent(requestDto.getContent());  // 'getContent' 사용
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        if (!commentRepository.existsByIdAndMemberId(commentId, memberId)) {
            throw new IllegalArgumentException("You are not authorized to delete this comment.");
        }
        commentRepository.deleteById(commentId);
    }
}
