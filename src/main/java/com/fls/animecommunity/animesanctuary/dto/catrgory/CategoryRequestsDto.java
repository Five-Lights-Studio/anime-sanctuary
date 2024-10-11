package com.fls.animecommunity.animesanctuary.dto.catrgory;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * Category의 Requests 의 DataTransferObject
 */

@Data
public class CategoryRequestsDto {
	@NotBlank(message = "Category name must not be blank")
	private String name;
}
