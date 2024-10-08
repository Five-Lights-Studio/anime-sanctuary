package com.fls.animecommunity.animesanctuary.dto.member;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

/*
 * Regex : 
 * 기본적으로 다 필수 입력
 * username(id) : 영어 대문자 소문자 숫자 허용 , 5-20자 
 * password : 영어 대문자 소문자 특수기호 , 8-72 , 영어 소문자, 득수문자 ,숫자 적어도 하나 포함 , 대문자는 필수아님
 * 
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterDto {
	
	@NotEmpty(message = "아이디는 필수 입력값입니다")
	@Pattern(regexp = "^[A-Za-z0-9]{5,20}$",message = "아이디는 영어 소문자와 숫자만 사용하여 5~20자리여야 합니다.")
    private String username;
	
	@NotEmpty(message = "비밀번호는 필수 입력값입니다")
	@Pattern(regexp = "^(?=.*[A-Za-z])"
			+ "(?=.*\\d)"
			+ "(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]"
			+ "{8,72}$", message = "비밀번호는 8~72자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;
    
	@NotEmpty(message = "이름은 필수 입력값입니다")
	private String name;
	
	@NotEmpty(message = "이메일은 필수 입력값입니다")
    @Email(message = "유효한 이메일 주소여야 합니다")
    private String email;
	
	@NotEmpty(message = "성별은 필수 입력값입니다")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "성별은 MALE, FEMALE, OTHER 중 하나여야 합니다.")
    private String gender;
	
	@NotNull(message = "생년월일은 필수 입력값입니다.")
	@Past(message = "생년월일은 과거의 날짜여야 합니다.")
    private LocalDate birth;
}
