package com.fls.animecommunity.animesanctuary.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fls.animecommunity.animesanctuary.dto.catrgory.CategoryRequestsDto;
import com.fls.animecommunity.animesanctuary.dto.catrgory.CategoryResponseDto;
import com.fls.animecommunity.animesanctuary.dto.catrgory.SuccessResponseDto;
import com.fls.animecommunity.animesanctuary.dto.note.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * Category와 관련된 CRUD 
 * 그러나 생성,조회,삭제는 AdminController로 뺐음.
 * 1.Category의 조회
 * 2.1개의 Category에 해당하는 Notes조회
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	// create category
	@PostMapping("/categories")
	public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestsDto requestsDto) {
		// 카테고리 생성 로직 호출
		CategoryResponseDto responseDto = categoryService.createCategory(requestsDto);
//			log.info("create category 실행");
		return ResponseEntity.ok(responseDto);
	}

	// 카테고리 삭제
	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<SuccessResponseDto> deleteNote(@PathVariable("categoryId") Long id) throws Exception {
//			log.info("delete category 실행");
		SuccessResponseDto responseDto = categoryService.deleteCategory(id);
		return ResponseEntity.ok(responseDto);
	}

	// 카테고리 수정
	@PostMapping("/categories/{categoryId}")
	public ResponseEntity<CategoryResponseDto> updateCategory(@Valid @PathVariable("categoryId") Long id,
			@RequestBody CategoryRequestsDto requestsDto) throws Exception {
//			log.info("update category 실행");
		CategoryResponseDto responseDto = categoryService.updateCategory(id, requestsDto);
		return ResponseEntity.ok(responseDto);
	}

	// Category 에 해당하는 Notes조회
	@GetMapping("/{categoryId}/notes")
	public ResponseEntity<?> getNotesByCategory(@PathVariable("categoryId") Long categoryId) {

		// log.info("getNotesByCategory 실행");
		List<NoteResponseDto> notes = categoryService.getNotesByCategory(categoryId);

		if (notes.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(notes);
	}

	// Category list
	@GetMapping
	public ResponseEntity<?> getCategories() {

		// log.info("getCategories 실행");
		List<CategoryResponseDto> categories = categoryService.getCategories();

		if (categories.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(categories);
	}

}
