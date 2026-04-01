package com.vpe.finalstore.order.security;

import com.vpe.finalstore.order.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("orderSecurity")
public class OrderSecurity {
    private final OrderRepository orderRepository;

    public boolean isOwner(Integer orderId, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();

        return orderRepository
            .findById(orderId)
            .map(order -> order.getCustomer().getUser().getUserId().equals(userId))
            .orElse(false);
    }
}
