package com.fls.animecommunity.animesanctuary.dto.catrgory;

import java.time.LocalDateTime;

import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.note.Note;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Category의 Response , 응답객체
 */

@Data
@NoArgsConstructor
public class CategoryResponseDto {
	private Long id;
    private String name;
    
    public CategoryResponseDto(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
