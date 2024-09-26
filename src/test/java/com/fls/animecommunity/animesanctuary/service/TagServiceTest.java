package com.fls.animecommunity.animesanctuary.service;

import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.model.tag.Tag;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import com.fls.animecommunity.animesanctuary.repository.TagRepository;
import com.fls.animecommunity.animesanctuary.service.impl.TagService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private TagService tagService;

    private Note note;
    private Tag tag;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContents("Test Content");

        tag = new Tag("Test Tag");
        tag.setId(1L);

        note.setTagEntities(new ArrayList<>());
    }

    // @Test
    // void addTag_shouldAddNewTagToNote() {
    //     when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
    //     when(tagRepository.findByName("Test Tag")).thenReturn(Optional.empty());
    //     when(tagRepository.save(any(Tag.class))).thenReturn(tag);

    //     Tag result = tagService.addTag(1L, "Test Tag");

    //     assertNotNull(result);
    //     assertEquals("Test Tag", result.getName());
    //     assertTrue(note.getTagEntities().contains(result));
    //     verify(tagRepository).save(any(Tag.class));
    // }

    @Test
    void addTag_shouldNotAllowDuplicateTagsOnNote() {
        note.getTagEntities().add(tag);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
        when(tagRepository.findByName("Test Tag")).thenReturn(Optional.of(tag));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.addTag(1L, "Test Tag");
        });

        assertEquals("이미 존재하는 태그입니다.", exception.getMessage());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void getTagsByNoteId_shouldReturnTagsForNote() {
        note.getTagEntities().add(tag);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        List<Tag> result = tagService.getTagsByNoteId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Tag", result.get(0).getName());
    }

    // @Test
    // void getNotesByTag_shouldReturnNotesForTag() {
    //     when(noteRepository.findByTagEntities_Name("Test Tag")).thenReturn(List.of(note));

    //     List<Note> result = tagService.getNotesByTag("Test Tag");

    //     assertNotNull(result);
    //     assertEquals(1, result.size());
    //     assertEquals("Test Note", result.get(0).getTitle());
    // }
}
