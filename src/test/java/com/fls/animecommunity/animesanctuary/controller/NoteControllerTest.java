package com.fls.animecommunity.animesanctuary.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.fls.animecommunity.animesanctuary.controller.NoteController;
import com.fls.animecommunity.animesanctuary.dto.note.NoteRequestsDto;
import com.fls.animecommunity.animesanctuary.dto.note.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.service.NoteService;

class NoteControllerTest {

    @Mock
    private NoteService noteService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private NoteController noteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNote_shouldReturnOk_whenValidRequestIsGiven() {
        NoteRequestsDto requestDto = new NoteRequestsDto();
        requestDto.setTitle("Test Note");
        requestDto.setContents("Test Contents");
        requestDto.setMemberId(1L);  // Logged-in user ID

        NoteResponseDto responseDto = new NoteResponseDto();

        when(noteService.createNote(any(NoteRequestsDto.class))).thenReturn(responseDto);

        ResponseEntity<?> response = noteController.createNote(requestDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getNote_shouldReturnOk_whenNoteExists() {
        NoteResponseDto responseDto = new NoteResponseDto();
        
        when(noteService.getNote(anyLong())).thenReturn(responseDto);

        ResponseEntity<NoteResponseDto> response = noteController.getNote(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
