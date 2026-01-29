package com.vpe.finalstore.order.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class OrderCreateDto {
    @NotNull
    private Integer customerId;

    @NotNull
    private Integer addressId;

    @NotEmpty
    private Set<OrderItemCreateDto> items;
}
