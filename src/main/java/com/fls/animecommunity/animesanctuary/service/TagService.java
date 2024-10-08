package com.fls.animecommunity.animesanctuary.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fls.animecommunity.animesanctuary.exception.ResourceNotFoundException;
import com.fls.animecommunity.animesanctuary.model.tag.Tag;
import com.fls.animecommunity.animesanctuary.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}