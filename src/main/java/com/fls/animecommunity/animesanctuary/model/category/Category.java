package com.fls.animecommunity.animesanctuary.model.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Category class 
 * field variable : 
 * 	2024/08/19 : id , name 만
 * id : GenerationType.IDENTITY
 */

@Entity
@Data
@Slf4j
@NoArgsConstructor
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
//	@Column(length = 500)
//    private String description;
}
