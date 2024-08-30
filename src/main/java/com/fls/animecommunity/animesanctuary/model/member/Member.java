package com.fls.animecommunity.animesanctuary.model.member;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * Member class : 회원 사용자 클래스
 * 기본적인 vaildation
 * id 의 GenerationType.IDENTITY
 */

@Getter
@Setter
@ToString
@Entity
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, nullable = false, unique = true)
	private String username;

	@Column(length = 100, nullable = false)
	private String password;

	@Column(length = 50, nullable = false)
	private String name;

	@Column(nullable = false)
	private LocalDate birth;

	@Column(length = 100)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GenderType gender;

	@Column(length = 255)
	private String profileImage;

	private String lineUserId;

	// 기본 생성자
	public Member() {
	}

	// 모든 필드를 포함한 생성자
	public Member(String username, String password, String name, LocalDate birth, String email, GenderType gender) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.birth = birth;
		this.email = email;
		this.gender = gender;
	}
}
