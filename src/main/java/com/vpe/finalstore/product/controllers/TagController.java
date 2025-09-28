package com.vpe.finalstore.product.controllers;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.dtos.TagCreateDto;
import com.vpe.finalstore.product.dtos.TagSummaryDto;
import com.vpe.finalstore.product.dtos.TagUpdateDto;
import com.vpe.finalstore.product.entities.Tag;
import com.vpe.finalstore.product.mappers.TagMapper;
import com.vpe.finalstore.product.repositories.TagRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @GetMapping
    public List<TagSummaryDto> getAllTags() {
        var tags = tagRepository.findAll();

        return tagMapper.toSummaryDto(tags);
    }

    @PostMapping
    public ResponseEntity<TagSummaryDto> createTag(@Valid @RequestBody TagCreateDto tagDto) {
        var tag = tagRepository.save(new Tag(tagDto.getName()));

        return ResponseEntity
            .created(URI.create("/api/tags/" + tag.getTagId()))
            .body(tagMapper.toSummaryDto(tag));
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<TagSummaryDto> updateTag(@PathVariable Integer tagId, @Valid @RequestBody TagUpdateDto tagDto) {
        var tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new NotFoundException("Tag not found"));

        tag.setName(tagDto.getName());

        tag = tagRepository.save(tag);

        return ResponseEntity.ok(tagMapper.toSummaryDto(tag));

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{tagId}")
    public void deleteTag(@PathVariable Integer tagId) {
        tagRepository.deleteById(tagId);
    }
}
