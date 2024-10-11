package com.fls.animecommunity.animesanctuary.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.fls.animecommunity.animesanctuary.exception.ResourceNotFoundException;
import com.fls.animecommunity.animesanctuary.model.tag.Tag;
import com.fls.animecommunity.animesanctuary.repository.TagRepository;

class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTag_shouldReturnCreatedTag() {
        Tag tag = new Tag();
        tag.setName("example-tag");

        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag createdTag = tagService.createTag(tag);

        assertNotNull(createdTag);
        assertEquals("example-tag", createdTag.getName());
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void getAllTags_shouldReturnListOfTags() {
        Tag tag1 = new Tag();
        tag1.setName("tag1");

        Tag tag2 = new Tag();
        tag2.setName("tag2");

        when(tagRepository.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        List<Tag> tags = tagService.getAllTags();

        assertNotNull(tags);
        assertEquals(2, tags.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void getTagById_shouldReturnTag_whenTagExists() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("example-tag");

        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));

        Tag foundTag = tagService.getTagById(1L);

        assertNotNull(foundTag);
        assertEquals("example-tag", foundTag.getName());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void getTagById_shouldThrowException_whenTagDoesNotExist() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(1L));
        assertNotNull(exception);
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void deleteTag_shouldDeleteTag() {
        doNothing().when(tagRepository).deleteById(anyLong());

        tagService.deleteTag(1L);

        verify(tagRepository, times(1)).deleteById(1L);
    }
}