package com.fls.animecommunity.animesanctuary.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.fls.animecommunity.animesanctuary.model.comment.Comment;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.repository.CommentRepository;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import com.fls.animecommunity.animesanctuary.service.CommentService;
import com.fls.animecommunity.animesanctuary.service.NotificationService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_shouldAddReply_whenParentCommentIdIsProvided() {
        Member member = new Member();
        member.setId(1L);
        member.setUsername("testUser");
        Note note = new Note();
        note.setId(1L);
        Comment parentComment = new Comment();
        parentComment.setId(1L);
        parentComment.setMember(member);
        parentComment.setNote(note);

        Comment reply = new Comment();
        reply.setId(2L);
        reply.setMember(member);
        reply.setNote(note);
        reply.setContent("This is a reply.");
        reply.setParentComment(parentComment);

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
        when(noteRepository.findById(anyLong())).thenReturn(Optional.of(note));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(reply);

        Comment savedReply = commentService.addComment("testUser", 1L, "This is a reply.", 1L);

        assertNotNull(savedReply);
        assertEquals("This is a reply.", savedReply.getContent());
        assertEquals(parentComment.getId(), savedReply.getParentComment().getId());

        // Verify that the notification service was called
        verify(notificationService).sendNotification(any(Member.class), any(Comment.class), anyString());
    }

    @Test
    void addComment_shouldNotAllowNestedReplies_whenParentCommentIdIsProvided() {
        Member member = new Member();
        member.setId(1L);
        member.setUsername("testUser");
        Note note = new Note();
        note.setId(1L);
        Comment parentComment = new Comment();
        parentComment.setId(1L);
        parentComment.setMember(member);
        parentComment.setNote(note);

        // Simulate that the parent comment is a reply itself (nested reply scenario)
        Comment grandParentComment = new Comment();
        grandParentComment.setId(2L);
        parentComment.setParentComment(grandParentComment);

        when(memberRepository.findByUsername(anyString())).thenReturn(Optional.of(member));
        when(noteRepository.findById(anyLong())).thenReturn(Optional.of(note));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(parentComment));

        try {
            commentService.addComment("testUser", 1L, "This should not be allowed", 1L);
        } catch (IllegalArgumentException e) {
            assertEquals("Nested replies are not allowed", e.getMessage());
        }

        // Verify that the notification service was not called
        verify(notificationService, never()).sendNotification(any(Member.class), any(Comment.class), anyString());
    }

    @Test
    void getCommentsByNoteId_shouldReturnComments_whenCommentsExist() {
        Note note = new Note();
        note.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setNote(note);
        comment.setContent("This is a comment.");

        when(commentRepository.findByNoteId(anyLong())).thenReturn(List.of(comment));

        List<Comment> comments = commentService.getCommentsByNoteId(1L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("This is a comment.", comments.get(0).getContent());
    }

    @Test
    void getCommentsByMemberId_shouldReturnComments_whenCommentsExist() {
        Member member = new Member();
        member.setId(1L);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setMember(member);
        comment.setContent("This is a comment.");

        when(commentRepository.findByMemberId(anyLong())).thenReturn(List.of(comment));

        List<Comment> comments = commentService.getCommentsByMemberId(1L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("This is a comment.", comments.get(0).getContent());
    }

    @Test
    void getReplies_shouldReturnReplies_whenRepliesExist() {
        Comment parentComment = new Comment();
        parentComment.setId(1L);
        Comment reply = new Comment();
        reply.setId(2L);
        reply.setParentComment(parentComment);
        reply.setContent("This is a reply.");

        when(commentRepository.findByParentCommentId(anyLong())).thenReturn(List.of(reply));

        List<Comment> replies = commentService.getReplies(1L);

        assertNotNull(replies);
        assertEquals(1, replies.size());
        assertEquals("This is a reply.", replies.get(0).getContent());
    }
}
