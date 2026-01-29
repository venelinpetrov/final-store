package com.vpe.finalstore.order.dtos;

import com.vpe.finalstore.order.enums.OrderStatusType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class OrderDto {
    private Integer orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatusType status;
    private Integer customerId;
    private Integer addressId;
    private Set<OrderItemDto> orderItems;
}
