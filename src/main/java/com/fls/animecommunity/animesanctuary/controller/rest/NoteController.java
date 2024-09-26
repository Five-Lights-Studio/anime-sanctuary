package com.fls.animecommunity.animesanctuary.controller.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteRequestsDto;
import com.fls.animecommunity.animesanctuary.model.note.dto.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.model.note.dto.SuccessResponseDto;
import com.fls.animecommunity.animesanctuary.service.impl.MemberService;
import com.fls.animecommunity.animesanctuary.service.interfaces.NoteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * NoteController 클래스 : Note의 CRUD 기능을 담당하며, API 매핑을 처리
 * 의존성 주입 : NoteService와 MemberService
 * 주요 메소드 : createNote, getNotes, getNote, updateNote, deleteNote
 * 파라미터 : NoteResponseDto, NoteRequestsDto, SuccessResponseDto
 */
@CrossOrigin(origins = {"http://localhost:9000", "http://127.0.0.1:5501"}) // 클라이언트의 도메인을 명시
@RestController
@RequiredArgsConstructor
@RequestMapping("api/notes")
@Slf4j
public class NoteController {

    // 의존성 주입
    private final NoteService noteService;
    private final MemberService memberService;
    
    //create Note
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createNote(@RequestPart("note") @Valid NoteRequestsDto requestsDto,
                                        @RequestPart("image") MultipartFile image,
                                        BindingResult result) {
        log.info("Received Note request with title: {} and contents: {}", requestsDto.getTitle(), requestsDto.getContents());

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        try {
            NoteResponseDto responseDto = noteService.createNoteWithImage(requestsDto, image);
            return ResponseEntity.ok(responseDto);
        } catch (IOException e) {
            log.error("Error occurred while uploading the image: {}", e.getMessage());
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }
    }

    //list Note
    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getNotes() {
        //log.info("getNotes 실행");
        List<NoteResponseDto> list = noteService.getNotes();
        return ResponseEntity.ok(list);
    }

    // 특정 ID로 노트 조회
    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> getNote(@PathVariable("noteId") Long id) {
        //log.info("getNote 실행");
        NoteResponseDto note = noteService.getNote(id);
        return ResponseEntity.ok(note);
    }

    // 노트 업데이트
    @PostMapping("/{noteId}")
    public ResponseEntity<?> updateNote(@Valid @PathVariable("noteId") Long id, 
                                        @RequestBody NoteRequestsDto requestsDto
                                        ,BindingResult result) throws Exception {
        //log.info("updateNote 실행");
        // 유효성 검사 오류 확인
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        
        NoteResponseDto updateNote = noteService.updateNote(id, requestsDto);
        return ResponseEntity.ok(updateNote);
    }

    // 노트 삭제
    @DeleteMapping("/{noteId}")
    public ResponseEntity<SuccessResponseDto> deleteNote(@PathVariable("noteId") Long id, 
                                                        @RequestParam("memberId") Long memberId) throws Exception {
        //log.info("deleteNote 실행");
        SuccessResponseDto responseDto = noteService.deleteNote(id, memberId);
        return ResponseEntity.ok(responseDto);
    }

    // 노트 검색
    @GetMapping("/search")
    public ResponseEntity<List<NoteResponseDto>> searchNotes(@RequestParam("keyword") String keyword) {
        List<NoteResponseDto> results = noteService.searchNotes(keyword);
        return ResponseEntity.ok(results);
    }

    // 노트 저장
    @PostMapping("/save/{noteId}")
    public ResponseEntity<?> saveNote(@PathVariable("noteId") Long noteId, HttpServletRequest request) {
        Member member = (Member) request.getSession().getAttribute("user");
        if (member == null) {
            return ResponseEntity.status(403).body("User must be logged in to save a note.");
        }

        boolean success = memberService.saveNoteForUser(member.getId(), noteId);
        if (success) {
            return ResponseEntity.ok("Note saved successfully.");
        } else {
            return ResponseEntity.status(404).body("Note not found.");
        }
    }

}
