package com.vpe.finalstore.tags.mappers;

import com.vpe.finalstore.tags.dtos.TagDto;
import com.vpe.finalstore.tags.entities.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);
    List<TagDto> toDto(List<Tag> tags);
}
