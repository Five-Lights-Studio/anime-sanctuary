package com.fls.animecommunity.animesanctuary.model.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoteRequestsDto {

    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    private Long categoryId;

    private Long memberId;  // Member 대신 memberId 사용
}
