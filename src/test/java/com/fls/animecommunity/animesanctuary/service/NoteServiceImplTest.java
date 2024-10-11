package com.fls.animecommunity.animesanctuary.service;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.fls.animecommunity.animesanctuary.dto.note.NoteRequestsDto;
import com.fls.animecommunity.animesanctuary.dto.note.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.repository.CategoryRepository;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;

class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private CategoryRepository categoryRepository;  // CategoryRepository를 목으로 추가

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNote_shouldReturnNoteResponseDto_whenNoteExists() {
        // Note 객체를 생성
        Note note = new Note();
        note.setTitle("Test Note");
        note.setContents("Test Contents");

        // Note 객체를 Optional로 감싸서 mock 리턴
        when(noteRepository.findById(anyLong())).thenReturn(Optional.of(note));

        // 실제로 반환할 NoteResponseDto를 기대
        NoteResponseDto responseDto = noteService.getNote(1L);

        assertNotNull(responseDto);
        assertEquals("Test Note", responseDto.getTitle());
        assertEquals("Test Contents", responseDto.getContents());
    }

    @Test
    void createNote_shouldReturnNoteResponseDto_whenValidRequestIsGiven() {
        NoteRequestsDto requestDto = new NoteRequestsDto();
        requestDto.setTitle("Test Note");
        requestDto.setContents("Test Contents");
        requestDto.setCategoryId(1L);
        requestDto.setMemberId(1L);  // 로그인된 사용자 ID 설정

        // 카테고리 목 객체 생성 및 반환
        Category category = new Category();
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        // 멤버 목 객체 생성 및 반환 (로그인된 사용자)
        Member member = new Member();
        member.setId(1L);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));  // 로그인된 사용자 반환

        // Note 객체 생성 및 저장 로직 목 설정
        Note savedNote = new Note();
        savedNote.setTitle("Test Note");
        savedNote.setContents("Test Contents");
        savedNote.setCategory(category);
        savedNote.setMember(member);  // 작성자 설정

        when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

        // NoteServiceImpl의 createNote 실행 및 결과 검증
        NoteResponseDto responseDto = noteService.createNote(requestDto);

        assertNotNull(responseDto);
        assertEquals("Test Note", responseDto.getTitle());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void updateNote_shouldThrowException_whenUserIsNotAuthorized() {
        NoteRequestsDto requestDto = new NoteRequestsDto();
        requestDto.setTitle("Updated Title");
        requestDto.setContents("Updated Contents");
        requestDto.setMemberId(2L);  // 다른 사용자 ID 설정

        // 멤버 목 객체 생성 및 반환 (로그인된 사용자)
        Member member = new Member();
        member.setId(1L);  // 실제 작성자 ID

        // Note 객체 생성 및 저장
        Note note = new Note();
        note.setTitle("Original Title");
        note.setContents("Original Contents");
        note.setMember(member);  // 작성자는 ID 1

        when(noteRepository.findById(anyLong())).thenReturn(Optional.of(note));

        // 다른 사용자가 수정하려고 하면 예외 발생
        assertThrows(IllegalStateException.class, () -> noteService.updateNote(1L, requestDto));
    }
}
