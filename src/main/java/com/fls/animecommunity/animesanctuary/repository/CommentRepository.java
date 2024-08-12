package com.fls.animecommunity.animesanctuary.repository;

import javax.xml.stream.events.Comment;
import java.util.List;
ㄴ
import org.springframework.data.jpa.repository.JpaRepository;
//리포지토리 클래스 : 데이터베이스와의 상호작용을 담당
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByBoard(Board board);
	
	//보드 import / Comment import 필요 

}

