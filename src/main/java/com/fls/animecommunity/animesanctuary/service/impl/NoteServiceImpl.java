package com.fls.animecommunity.animesanctuary.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    // 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getNotes() {
        List<Note> notes = noteRepository.findAllByOrderByModifiedAtDesc();
        List<NoteResponseDto> noteResponseDtos = new ArrayList<>();

        for (Note note : notes) {
            noteResponseDtos.add(new NoteResponseDto(note));
        }

        return noteResponseDtos;
    }

    // 개별 노트 조회
    @Override
    @Transactional
    public NoteResponseDto getNote(Long id) {
        Optional<Note> optionalNote = noteRepository.findById(id);

        if (optionalNote.isPresent()) {
            return new NoteResponseDto(optionalNote.get());
        } else {
            throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
        }
    }

    // 노트 생성
    @Override
    @Transactional
    public NoteResponseDto createNote(NoteRequestsDto requestsDto) {
        Member member = memberRepository.findById(requestsDto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Note note = new Note(requestsDto.getTitle(), requestsDto.getContents(), member);
        note.setCategory(categoryRepository.findById(requestsDto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found")));

        noteRepository.save(note);
        
        return new NoteResponseDto(note);
    }

    // 노트 업데이트
    @Override
    @Transactional
    public NoteResponseDto updateNote(Long id, NoteRequestsDto requestsDto) throws Exception {
        Optional<Note> optionalNote = noteRepository.findById(id);

        if (!optionalNote.isPresent()) {
            throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
        }

        Note note = optionalNote.get();
        note.update(requestsDto);
        
        return new NoteResponseDto(note);
    }

    // 노트 삭제
    @Override
    @Transactional
    public SuccessResponseDto deleteNote(Long id, NoteRequestsDto requestsDto) throws Exception {
        Optional<Note> optionalNote = noteRepository.findById(id);

        if (!optionalNote.isPresent()) {
            throw new IllegalArgumentException("아이디가 존재하지 않습니다.");
        }

        noteRepository.deleteById(id);
        
        return new SuccessResponseDto(true);
    }

    // 노트 검색
    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> searchNotes(String keyword) {
        List<Note> notes = noteRepository.findByTitleContainingOrContentsContaining(keyword, keyword);
        return notes.stream().map(NoteResponseDto::new).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public NoteResponseDto createNoteWithImage(NoteRequestsDto requestsDto, MultipartFile image) throws IOException {
        Member member = memberRepository.findById(requestsDto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Note note = new Note(requestsDto.getTitle(), requestsDto.getContents(), member);

        // 카테고리 설정 (필요 시)
        if (requestsDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestsDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            note.setCategory(category);
        }

        // 이미지 업로드 처리
        if (image != null && !image.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads/note-images/" + member.getId();
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + uploadDir);
                }
            }

            String originalFilename = image.getOriginalFilename();
            String filePath = uploadDir + "/" + UUID.randomUUID().toString() + "_" + originalFilename;
            File destinationFile = new File(filePath);
            image.transferTo(destinationFile);

            // 노트에 이미지 경로 저장
            note.setImagePath(filePath);
        }

        noteRepository.save(note);
        return new NoteResponseDto(note);
    }

}
