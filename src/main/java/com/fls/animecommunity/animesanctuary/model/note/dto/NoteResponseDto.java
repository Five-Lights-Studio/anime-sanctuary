package com.fls.animecommunity.animesanctuary.model.note.dto;

import java.time.LocalDateTime;

import com.fls.animecommunity.animesanctuary.model.note.Note;

import lombok.Data;

@Data
public class NoteResponseDto {

    private Long id;
    private String title;
    private String contents;
    private String author;  // 작성자 이름 또는 ID
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long categoryId;

    // 기본 생성자 추가
    public NoteResponseDto() {
    }

    public NoteResponseDto(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.contents = note.getContents();
        this.author = note.getMember().getUsername();  // 작성자의 이름이나 유저네임
        this.createdAt = note.getCreatedAt();
        this.modifiedAt = note.getModifiedAt();
        if (note.getCategory() != null) {
            this.categoryId = note.getCategory().getId();
        } else {
            this.categoryId = null;
        }
    }
}
