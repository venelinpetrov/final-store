package com.vpe.finalstore.order.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemCreateDto {
    @NotNull
    private Integer variantId;

    @NotNull
    @Min(1)
    private short quantity;
}
