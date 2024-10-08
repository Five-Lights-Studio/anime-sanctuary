package com.fls.animecommunity.animesanctuary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fls.animecommunity.animesanctuary.dto.catrgory.CategoryRequestsDto;
import com.fls.animecommunity.animesanctuary.dto.catrgory.CategoryResponseDto;
import com.fls.animecommunity.animesanctuary.dto.catrgory.SuccessResponseDto;
import com.fls.animecommunity.animesanctuary.dto.note.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.exception.ResourceNotFoundException;
import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.repository.CategoryRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
	
	
	private final CategoryRepository categoryRepository;
	private final NoteRepository noteRepository;
	
	//getNotesByCategory 특정 category별 Notes 목록 조회
	@Transactional(readOnly = true)
	public List<NoteResponseDto> getNotesByCategory(Long categoryId ) {
		
		//		log.info("getNotesByCategory 실행");
		//		log.info("categoryId :{}",categoryId );
		
		List<Note> notes = noteRepository.findByCategoryId(categoryId);
		//		log.debug("Found {} notes for category ID: {}", notes.size(), categoryId );
		
		List<NoteResponseDto> noteResponseDtos = new ArrayList<>();
        for (Note note : notes) {
            NoteResponseDto dto = new NoteResponseDto(note);
            noteResponseDtos.add(dto);
        }
        return noteResponseDtos;
	}
	
	//find All categoies 모든 카테고리를 가져옴
	@Transactional(readOnly = true)
	public List<CategoryResponseDto> getCategories() {
		//		log.info("getCategories() 실행");
		//      log.debug("Found {} categories", categories.size());
		
		 List<Category> categories = categoryRepository.findAll();
	        List<CategoryResponseDto> categoryResponseDtos = new ArrayList<>();
	        for (Category category : categories) {
	            CategoryResponseDto dto = new CategoryResponseDto(category);
	            categoryResponseDtos.add(dto);
	        }
	        return categoryResponseDtos;
	}
	
	//write , create
	@Transactional
	public CategoryResponseDto createCategory(CategoryRequestsDto requestsDto) {
//		log.info("createCategory() 실행");
		
		Category category = new Category();
		category.setName(requestsDto.getName());
		
		Category savedCategory = categoryRepository.save(category);
//		log.info("Category created successfully with ID: {}", savedCategory.getId());
		
		return new CategoryResponseDto(savedCategory);
	}
	
	//delete
	@Transactional
	public SuccessResponseDto deleteCategory(Long id) throws Exception{
//		log.info("deleteCategory() 실행");
//		log.info("Deleting category with ID: {}", id);
		
		Optional<Category> optionalCategory = categoryRepository.findById(id);

		if (!optionalCategory.isPresent()) {
		    throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
		}

		Category category = optionalCategory.get();
		
		categoryRepository.deleteById(id);
//		log.info("Category deleted successfully with ID: {}", id);
		
		return new SuccessResponseDto(true);
	}
	
	//update
	@Transactional
	public CategoryResponseDto updateCategory(Long id, CategoryRequestsDto requestsDto) {
//		log.info("updateCategory() 실행");
//		log.info("Updating category with ID: {}", id);
		
		Optional<Category> optionalCategory = categoryRepository.findById(id);

		if (!optionalCategory.isPresent()) {
		    throw new ResourceNotFoundException("Category not found with id: " + id);
		}

		Category category = optionalCategory.get();

	    category.setName(requestsDto.getName());
	    Category updatedCategory = categoryRepository.save(category);
//	    log.info("Category updated successfully with ID: {}", updatedCategory.getId());
	    
	    return new CategoryResponseDto(updatedCategory);
	}
}
