package com.vpe.finalstore.cart.services;

import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.repositories.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    public Optional<Cart> getCartWithItems(Integer cartId) {
        var x=  cartRepository.getCartWithItems(cartId);

        return x;
    }
}
