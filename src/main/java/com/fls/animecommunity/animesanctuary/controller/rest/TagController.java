package com.fls.animecommunity.animesanctuary.controller.rest;

import com.fls.animecommunity.animesanctuary.controller.dto.TagRequest;
import com.fls.animecommunity.animesanctuary.model.note.Note;
import com.fls.animecommunity.animesanctuary.model.tag.Tag;
import com.fls.animecommunity.animesanctuary.service.impl.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/add")
    public ResponseEntity<Tag> addTag(@RequestBody TagRequest tagRequest) {
        Tag tag = tagService.addTag(tagRequest.getNoteId(), tagRequest.getTagName());
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Tag>> getTagsByNoteId(@PathVariable Long noteId) {
        List<Tag> tags = (List<Tag>) tagService.getTagsByNoteId(noteId);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> getNotesByTag(@RequestParam String tagName) {
        List<Note> notes = tagService.getNotesByTag(tagName);
        return ResponseEntity.ok(notes);
    }
}
