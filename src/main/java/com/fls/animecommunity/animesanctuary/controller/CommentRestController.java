package com.fls.animecommunity.animesanctuary.controller;

import java.time.LocalDateTime;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.hibernate.mapping.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fls.animecommunity.animesanctuary.model.Comment;
import com.fls.animecommunity.animesanctuary.repository.CommentRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


//컨트롤러 클래스 :HTTP 요청을 처리하고, 서비스 계층과 통신하여 클라이언트에 응답을 반환

@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
@Slf4j
public class CommentRestController {
	
	//private final BoardRepository boardRepository; <- 나중에 맞출것
	private final CommentRepository commentRepository;
	
	//리플 등록
	@PostMapping("/api/posts/{postId}/comments")
	public ResponseEntity<String> writeReply(@SessionAttribute("loginMember") Member loginMember,
											 @PathVariable("board_id") Long board_id, 
											 @ModelAttribute Comment comment) {
//		log.info("board_id:{}", board_id);
		
		//어떤 글의 리플인가
		comment.setBoard(boardRepository.findById(board_id).get());
		
		//누가 썼는지 member
		comment.setMember(loginMember);
		
		//리플 쓴 시간
		comment.setCreated_time(LocalDateTime.now());
		
		log.info("comment:{}", comment);

		//DB등록
		CommentRepository.save(comment);
		
		return ResponseEntity.ok("등록 성공");
	}
	
	//리플 읽기(/reply/게시물아이디/리플아이디)
	@GetMapping("/api/posts/{postId}/comments")
	public ResponseEntity<Comment> findComment(@PathVariable("board_id") Long board_id,
										   @PathVariable("comment_id") Long comment_id) {
		Comment comment = null; //DB에서 검색한 댓글
		return ResponseEntity.ok(comment);
	}
	
	//리플 목록
	@GetMapping("/api/posts/{postId}/comments")
	public ResponseEntity<List<Comment>> findReplies(@SessionAttribute("loginMember") Member loginMember,
												   @PathVariable("board_id") Long board_id) {
		Board board = new Board();
		board.setId(board_id);
		List<Comment> comments = commentRepository.findByBoard(board); //DB에서 가져오기
//		log.info("리플들:{}", replies);
		//수정삭제 권한 여부
		if(comments != null && comments.size() > 0) {
			for(Comment r : comments) {
				if(r.getMember().getMember_id().equals(loginMember.getMember_id())) {
					//권한 부여
					r.setWriter(true);
				}
			}
		}
		
		return ResponseEntity.ok(comments);
	}
	
	//리플 수정
	@PutMapping("/api/comments/{commentId}")
	public ResponseEntity<Comment> updateReply(@SessionAttribute("loginMember") Member loginMember,
											 @PathVariable("board_id") Long board_id,
											 @PathVariable("reply_id") Long reply_id,
											 @ModelAttribute Comment comment) {
		//수정권한이 있는지 체크
		Comment findComment = CommentRepository.findById(comment_id).get();
		if(findComment.getMember().getMember_id().equals(loginMember.getMember_id())) {
			findComment.setContent(comment.getComment());
			commentRepository.save(findComment);
		}
		
		return ResponseEntity.ok(comment); //수정된 리플객체를 리턴
	}
	
	//리플 삭제
	@DeleteMapping("/api/comments/{commentId}")
	public ResponseEntity<String> removeComment(@SessionAttribute("loginMember") Member loginMember,
											  @PathVariable("board_id") Long board_id,
											  @PathVariable("comment_id") Long comment_id) {
		//삭제권한이 있는지 체크
		Comment findComment = CommentRepository.findById(comment_id).get();
		if(findComment.getMember().getMember_id().equals(loginMember.getMember_id())) {
			commentRepository.deleteById(comment_id);
		}
		
		return ResponseEntity.ok("삭제 성공");
	}

				/*댓글 작성
				 댓글 목록: 엔드포인트:/api/posts/{postId}/comments (GET 요청)
				댓글 작성: 엔드포인트:/api/posts/{postId}/comments (POST 요청)
				댓글 수정: 엔드포인트:/api/comments/{commentId} (PUT 요청)
				댓글 삭제: 엔드포인트:/api/comments/{commentId} (DELETE 요청) 

				 * 
				 * */
	}
}
