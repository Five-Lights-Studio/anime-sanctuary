package com.fls.animecommunity.animesanctuary.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fls.animecommunity.animesanctuary.dto.note.NoteRequestsDto;
import com.fls.animecommunity.animesanctuary.dto.note.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.dto.note.SuccessResponseDto;
import com.fls.animecommunity.animesanctuary.model.member.Member;
import com.fls.animecommunity.animesanctuary.service.MemberService;
import com.fls.animecommunity.animesanctuary.service.NoteService;

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
@RestController
@RequiredArgsConstructor
@RequestMapping("api/notes")
@Slf4j
public class NoteController {

    // 의존성 주입
    private final NoteService noteService;
    private final MemberService memberService;

 // 노트 생성
    @PostMapping
    public ResponseEntity<?> createNote(@SessionAttribute("user") Member member,
                                        @Valid @RequestBody NoteRequestsDto requestsDto,
                                        BindingResult result) {

        log.info("createNote 실행");
        log.info("Received Note request with title: {} and contents: {}", requestsDto.getTitle(), requestsDto.getContents());

        // 유효성 검사 오류 확인
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 세션에서 로그인한 사용자의 Id를 가져옴
        Long memberId = member.getId();
        log.info("Logged in memberId: {}", memberId);

        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Member ID not found.");
        }

        // dto에 memberId 설정
        requestsDto.setMemberId(memberId);

        // 노트 생성 로직 처리
        NoteResponseDto responseDto = noteService.createNote(requestsDto);

        return ResponseEntity.ok(responseDto);
    }


    // 노트 목록 조회
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
    public ResponseEntity<?> updateNote(@SessionAttribute("user") Member member
    									,@Valid @PathVariable("noteId") Long id, 
                                        @RequestBody NoteRequestsDto requestsDto
                                        ,BindingResult result) throws Exception {
        //log.info("updateNote 실행");
        // 유효성 검사 오류 확인
    	if (result.hasErrors()) {
            // 오류 메시지를 리스트로 변환
            List<String> errorMessages = result.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errorMessages); // 에러 메시지를 JSON 배열로 반환
        }
        
        NoteResponseDto updateNote = noteService.updateNote(id, requestsDto);
        return ResponseEntity.ok(updateNote);
    }

    // 노트 삭제
    @DeleteMapping("/{noteId}")
    public ResponseEntity<SuccessResponseDto> deleteNote(@SessionAttribute("user") Member member
    													,@PathVariable("noteId") Long id, 
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

    // 노트 저장 (bookmark)
    @PostMapping("/save/{noteId}")
    public ResponseEntity<?> saveNote(@SessionAttribute("user") Member member
    								,@PathVariable("noteId") Long noteId, HttpServletRequest request) {
        Member member2 = (Member) request.getSession().getAttribute("user");
        if (member2 == null) {
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
