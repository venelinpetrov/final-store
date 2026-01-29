package com.vpe.finalstore.order.security;

import com.vpe.finalstore.order.repositories.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
public class OrderSecurity {
    private final OrderRepository orderRepository;

    public OrderSecurity(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean canViewOrder(Integer orderId, Authentication authentication) {
        Integer userId = Integer.valueOf(authentication.getName());

        return orderRepository
            .findById(orderId)
            .map(order -> order.getCustomer().getUser().getUserId().equals(userId))
            .orElse(false);
    }
}
