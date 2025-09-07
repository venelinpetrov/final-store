package com.vpe.finalstore.tags.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class TagSummaryDto {
    private Integer tagId;
    private String name;
}