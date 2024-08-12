package com.fls.animecommunity.animesanctuary.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity //테이블 생성
@Builder @Getter @Setter @ToString  
// 엔티티 클래스 : 댓글을 표현하는 데이터 모델
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// ㄴ기본 키 값 자동 생성
	private long id; // 아이디
	
	private String username; //작성자

	@Column(length = 1000, nullable = false) // <-열의 최대 길이 제한걸때 사용/nullx
	private String comments; //댓글내용
	//Column 제약사항 집어넣기
	private LocalDateTime created_time;  //날짜
	
	//필요하다면 private int likes = 0; //좋아요 수
	

	
	// ------------추후 고려 -------------
	//private String parentId <-댓글이 달린 글? 선택
	
	//@ManyToOne
	//private Board board; 게시판 나중에  //<-★게시판추가시
	
	//@ManyToOne
	//@JoinColumn(name="member_id")
	//private Member member; 멤버 넣으세요//<-★회원추가시
	
}