package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductImageAssignmentDto {
    @NotNull
    List<Integer> imageIds;
}
