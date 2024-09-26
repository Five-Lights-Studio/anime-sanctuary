package com.fls.animecommunity.animesanctuary.repository;

import com.fls.animecommunity.animesanctuary.model.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNoteId(Long noteId);
    List<Comment> findByMemberId(Long memberId);
    List<Comment> findByParentCommentId(Long parentCommentId);
    // Member ID와 Comment ID로 존재 여부 확인
    boolean existsByIdAndMemberId(Long commentId, Long memberId);
}
