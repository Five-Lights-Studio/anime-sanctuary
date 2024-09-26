package com.fls.animecommunity.animesanctuary.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.fls.animecommunity.animesanctuary.controller.rest.NoteController;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteRequestsDto;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.service.interfaces.NoteService;
import org.springframework.mock.web.MockMultipartFile;
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
    void createNote_shouldReturnOk_whenValidRequestIsGiven() throws IOException {
        NoteRequestsDto requestDto = new NoteRequestsDto();
        requestDto.setTitle("Test Note");
        requestDto.setContents("Test Contents");
        requestDto.setMemberId(1L);  // Logged-in user ID

        MockMultipartFile mockFile = new MockMultipartFile("image", "test.png", "image/png", new byte[0]);

        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContents("Test Contents");
        
        NoteResponseDto responseDto = new NoteResponseDto(note);

        when(noteService.createNoteWithImage(any(NoteRequestsDto.class), any(MultipartFile.class)))
            .thenReturn(responseDto);

        ResponseEntity<?> response = noteController.createNote(requestDto, mockFile, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getNote_shouldReturnOk_whenNoteExists() {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setContents("Test Contents");
        // 필요한 필드들을 초기화해줍니다.
        
        NoteResponseDto responseDto = new NoteResponseDto(note);

        when(noteService.getNote(anyLong())).thenReturn(responseDto);

        ResponseEntity<NoteResponseDto> response = noteController.getNote(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
