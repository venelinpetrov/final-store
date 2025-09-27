package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.dtos.TagDto;
import com.vpe.finalstore.product.dtos.TagSummaryDto;
import com.vpe.finalstore.product.entities.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);

    List<TagDto> toDto(List<Tag> tags);

    TagSummaryDto toSummaryDto(Tag tag);

    List<TagSummaryDto> toSummaryDto(List<Tag> tags);
}