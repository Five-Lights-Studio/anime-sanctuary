package com.fls.animecommunity.animesanctuary.controller;

import com.fls.animecommunity.animesanctuary.dto.note.NoteResponseDto;
import com.fls.animecommunity.animesanctuary.service.NoteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("topic")
@RequiredArgsConstructor
public class PageController {

    private final NoteService noteService;

    @GetMapping("list")
    public String list() {
        return "List of topics";
    }

    @GetMapping("read/{note_id}")
    public NoteResponseDto getPost(@PathVariable("note_id") Long id) {
        return noteService.getNote(id);
    }

    @GetMapping("write")
    public String write() {
        return "Write a new topic";
    }

    @GetMapping("update/{note_id}")
    public String update(@PathVariable("note_id") Long id) {
        return "Update topic with ID: " + id;
    }
}
