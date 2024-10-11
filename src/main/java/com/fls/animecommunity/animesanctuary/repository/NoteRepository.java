package com.fls.animecommunity.animesanctuary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fls.animecommunity.animesanctuary.model.note.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
	
	List<Note> findAllByOrderByModifiedAtDesc();
	List<Note> findByCategoryIdOrderByModifiedAtDesc(Long categoryId);
	List<Note> findByCategoryId(Long categoryId);
	
	// 제목이나 내용에 검색어가 포함된 노트를 찾는 메서드
    List<Note> findByTitleContainingOrContentsContaining(String titleKeyword, String contentsKeyword);
}
