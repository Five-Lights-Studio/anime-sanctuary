package com.fls.animecommunity.animesanctuary.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fls.animecommunity.animesanctuary.exception.ResourceNotFoundException;
import com.fls.animecommunity.animesanctuary.model.category.Category;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteRequestsDto;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.model.note.dto.SuccessResponseDto;
import com.fls.animecommunity.animesanctuary.repository.CategoryRepository;
import com.fls.animecommunity.animesanctuary.repository.MemberRepository;
import com.fls.animecommunity.animesanctuary.repository.NoteRepository;
import com.fls.animecommunity.animesanctuary.service.interfaces.NoteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    // 상수로 중복된 메시지 정의
    private static final String ID_NOT_FOUND_MESSAGE = "아이디가 존재하지 않습니다.: ";

    // 모든 노트 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getNotes() {
        log.info("수정일 기준으로 정렬된 모든 노트를 가져옵니다.");
        List<Note> notes = noteRepository.findAllByOrderByModifiedAtDesc();
        return notes.stream()
                    .map(NoteResponseDto::new)
                    .collect(Collectors.toList());
    }

    // 특정 ID로 노트 조회
    @Override
    @Transactional(readOnly = true)
    public NoteResponseDto getNote(Long id) {
        log.info("ID로 노트를 조회합니다. ID: {}", id);
        Note note = noteRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ID_NOT_FOUND_MESSAGE + id));
        return new NoteResponseDto(note);
    }

    // 새로운 노트 생성 (인증된 사용자 필요)
    @Override
    @Transactional
    public NoteResponseDto createNote(NoteRequestsDto requestsDto) {
        log.info("새로운 노트를 생성합니다. 제목: {}", requestsDto.getTitle());

        // 로그인된 사용자 확인
        Member member = memberRepository.findById(requestsDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 카테고리 유효성 검사
        Category category = categoryRepository.findById(requestsDto.getCategoryId())
                                .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다. ID: " + requestsDto.getCategoryId()));

        // 노트 생성
        Note note = new Note();
        note.setTitle(requestsDto.getTitle());
        note.setContents(requestsDto.getContents());
        note.setCategory(category);
        note.setMember(member);  // 작성자 정보 추가

        Note savedNote = noteRepository.save(note);
        log.info("노트가 성공적으로 생성되었습니다. ID: {}", savedNote.getId());

        return new NoteResponseDto(savedNote);
    }

    // 기존 노트 수정 (인증된 사용자 필요)
    @Override
    @Transactional
    public NoteResponseDto updateNote(Long id, NoteRequestsDto requestsDto) {
        log.info("ID로 노트를 수정합니다. ID: {}", id);

        Note note = noteRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ID_NOT_FOUND_MESSAGE + id));

        // 작성자가 일치하는지 확인
        if (!note.getMember().getId().equals(requestsDto.getMemberId())) {
            throw new IllegalStateException("이 노트를 수정할 권한이 없습니다.");
        }

        note.update(requestsDto);
        log.info("노트가 성공적으로 수정되었습니다. ID: {}", id);

        return new NoteResponseDto(note);
    }

    // 노트 삭제 (인증된 사용자 필요)
    @Override
    @Transactional
    public SuccessResponseDto deleteNote(Long id, Long memberId) {
        log.info("ID로 노트를 삭제합니다. ID: {}", id);

        Note note = noteRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ID_NOT_FOUND_MESSAGE + id));

        // 작성자가 일치하는지 확인
        if (!note.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("이 노트를 삭제할 권한이 없습니다.");
        }

        noteRepository.deleteById(id);
        log.info("노트가 성공적으로 삭제되었습니다. ID: {}", id);

        return new SuccessResponseDto(true);
    }

    // 키워드로 노트 검색
    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> searchNotes(String keyword) {
        log.info("키워드로 노트를 검색합니다. 키워드: {}", keyword);

        List<Note> notes = noteRepository.findByTitleContainingOrContentsContaining(keyword, keyword);
        return notes.stream()
                    .map(NoteResponseDto::new)
                    .collect(Collectors.toList());
    }
}
