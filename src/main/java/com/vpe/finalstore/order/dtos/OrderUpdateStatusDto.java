package com.vpe.finalstore.order.dtos;

import com.vpe.finalstore.order.enums.OrderStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateStatusDto {
    @NotNull
    private OrderStatusType status;
}

