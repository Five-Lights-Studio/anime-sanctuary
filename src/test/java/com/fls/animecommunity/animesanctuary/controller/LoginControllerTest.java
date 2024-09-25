package com.fls.animecommunity.animesanctuary.controller;

import com.fls.animecommunity.animesanctuary.controller.rest.PageController;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.service.interfaces.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PageControllerTest {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private PageController pageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pageController).build();
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(get("/topic/list"))
                .andExpect(status().isOk())
                .andExpect(content().string("List of topics"));
    }

    @Test
    void testGetPost() throws Exception {
        Long noteId = 1L;
        NoteResponseDto noteResponseDto = new NoteResponseDto(); // 필요한 필드 초기화
        when(noteService.getNote(noteId)).thenReturn(noteResponseDto);

        mockMvc.perform(get("/topic/read/{note_id}", noteId))
                .andExpect(status().isOk())
                .andExpect(content().json("{}")); // 빈 JSON 객체가 반환되는 것을 기대함
    }

    @Test
    void testWrite() throws Exception {
        mockMvc.perform(get("/topic/write"))
                .andExpect(status().isOk())
                .andExpect(content().string("Write a new topic"));
    }

    @Test
    void testUpdate() throws Exception {
        Long noteId = 1L;
        mockMvc.perform(get("/topic/update/{note_id}", noteId))
                .andExpect(status().isOk())
                .andExpect(content().string("Update topic with ID: 1"));
    }
}
