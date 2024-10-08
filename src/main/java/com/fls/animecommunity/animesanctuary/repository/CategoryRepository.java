package com.fls.animecommunity.animesanctuary.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fls.animecommunity.animesanctuary.dto.note.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.note.Note;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	
}
