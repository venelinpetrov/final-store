package com.vpe.finalstore.order.mappers;

import com.vpe.finalstore.order.dtos.OrderDto;
import com.vpe.finalstore.order.dtos.OrderItemDto;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "status", source = "status.name")
    @Mapping(target = "customerId", source = "customer.customerId")
    @Mapping(target = "addressId", source = "address.addressId")
    OrderDto toDto(Order order);

    List<OrderDto> toDto(List<Order> orders);

    @Mapping(target = "variantId", source = "variant.variantId")
    OrderItemDto toDto(OrderItem orderItem);

    Set<OrderItemDto> toDto(Set<OrderItem> orderItems);
}
