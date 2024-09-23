package com.fls.animecommunity.animesanctuary.service.impl;

import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.model.tag.Tag;
import com.fls.animecommunity.animesanctuary.repository.TagRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final NoteRepository noteRepository;

    @Transactional
    public Tag addTag(Long noteId, String tagName) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노트를 찾을 수 없습니다."));

        Tag tag = tagRepository.findByName(tagName)
                .orElse(new Tag(tagName));

        if (note.getTagEntities().contains(tag)) {
            throw new IllegalArgumentException("이미 존재하는 태그입니다.");
        }

        note.getTagEntities().add(tag);  // 새로운 태그 엔티티 리스트에 추가
        tag.getNotes().add(note);

        return tagRepository.save(tag);
    }

    public List<Tag> getTagsByNoteId(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노트를 찾을 수 없습니다."));
        return note.getTagEntities();  // 새로운 태그 엔티티 리스트 반환
    }

    public List<Note> getNotesByTag(String tagName) {
        return noteRepository.findByTags_Name(tagName);
    }
}
