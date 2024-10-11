package com.fls.animecommunity.animesanctuary.dto.note;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Note의 Requests 의 DataTransferObject
 * title : 영어 소문자 대문자 숫자 히라가나 가타카나 한자 모든 특수문자 허용 , 1-100자
 * contents : 영어 소문자 대문자 숫자 히라가나 가타카나 한자 모든 특수문자 허용 , 1-10000자 / contents 타입을 TEXT로 바꿈
 * categoryId : 얘는 일단은 필수 
 * memberId : 일단 요구하고 있는데 나중에 없도록 수정할거임
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteRequestsDto {
	
    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(min = 1, max = 100, message = "제목은 1자 100자 이내로 작성해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9ぁ-んァ-ン一-龥々ー\\s\\p{Punct}]*$", message = "タイトルには、英数字、ひらがな、カタカナ、漢字、空白のみ使用できます。")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    @Size(min = 1, max = 10000, message = "内容は1文字以上10000文字以内で入力してください。")
    @Pattern(regexp = "^[a-zA-Z0-9ぁ-んァ-ン一-龥々ー\\s\\p{Punct}]*$", message = "内容には、英数字、ひらがな、カタカナ、漢字、空白、およびすべての特殊文字が使用できます。")
    private String contents;

    private Long categoryId;

    private Long memberId;
}
