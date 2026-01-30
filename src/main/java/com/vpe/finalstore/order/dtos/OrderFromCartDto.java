package com.vpe.finalstore.order.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFromCartDto {
    @NotNull
    private Integer customerId;

    @NotNull
    private Integer addressId;
}

