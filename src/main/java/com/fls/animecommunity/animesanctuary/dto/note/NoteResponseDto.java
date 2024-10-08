package com.fls.animecommunity.animesanctuary.dto.note;

import java.time.LocalDateTime;

import com.fls.animecommunity.animesanctuary.model.note.Note;

import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Note의 Response , 응답객체
 */

@Getter
@NoArgsConstructor
public class NoteResponseDto {
	private Long id;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long categoryId;
//    private String errorMessage;
    
    public NoteResponseDto(Note entity) {
        this.id = entity.getId();
        this.title = entity.getTitle() != null ? entity.getTitle() : "No title";  // null 체크
        this.contents = entity.getContents() != null ? entity.getContents() : "No contents";  // null 체크
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.categoryId = entity.getCategory() != null ? entity.getCategory().getId() : null;
    }    
}

