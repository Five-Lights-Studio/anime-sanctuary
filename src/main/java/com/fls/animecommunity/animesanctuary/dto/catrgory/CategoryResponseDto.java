package com.fls.animecommunity.animesanctuary.dto.catrgory;

import com.fls.animecommunity.animesanctuary.model.category.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

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
