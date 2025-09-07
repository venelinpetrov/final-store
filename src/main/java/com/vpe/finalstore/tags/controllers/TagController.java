package com.vpe.finalstore.tags.controllers;

import com.vpe.finalstore.tags.dtos.TagDto;
import com.vpe.finalstore.tags.mappers.TagMapper;
import com.vpe.finalstore.tags.repositories.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @GetMapping
    public List<TagDto> getTagsWithProducts() {
        var tags = tagRepository.findAll();

        return tagMapper.toDto(tags);
    }
}
