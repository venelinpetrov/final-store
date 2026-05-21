package com.vpe.finalstore.cart.security;

import com.vpe.finalstore.cart.repositories.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component("cartSecurity")
public class CartSecurity {
    private final CartRepository cartRepository;

    public boolean isOwner(UUID cartId, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();

        return cartRepository
            .findById(cartId)
            .map(cart -> {
                if (cart.getCustomer() != null) {
                    return cart.getCustomer().getUser().getUserId().equals(userId);
                }
                return false;
            })
            .orElse(false);
    }
}
