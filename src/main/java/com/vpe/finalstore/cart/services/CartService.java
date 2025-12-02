package com.vpe.finalstore.cart.services;

import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.entities.CartItem;
import com.vpe.finalstore.cart.exceptions.CartNotFoundException;
import com.vpe.finalstore.cart.repositories.CartRepository;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;

    public Optional<Cart> getCartWithItems(UUID cartId) {
        return cartRepository.getCartWithItems(cartId);
    }

    public Cart createCart() {
        return  cartRepository.save(new Cart());
    }

    public CartItem addToCart(UUID cartId, Integer variantId) {
        var cart = cartRepository.getCartWithItems(cartId)
            .orElseThrow(CartNotFoundException::new);
        var variant = variantRepository.findByVariantId(variantId)
            .orElseThrow(VariantNotFoundException::new);

        var cartItem = cart.addItem(variant);

        cartRepository.save(cart);

        return cartItem;
    }

    public CartItem updateCartItem(UUID cartId, Integer variantId, short quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        var cartItem = cart.getItem(variantId);

        if (cartItem == null) {
            throw new VariantNotFoundException();
        }

        cartItem.setQuantity(quantity);

        cartRepository.save(cart);

        return cartItem;
    }

    public void deleteCartItem(UUID cartId, Integer variantId) {
        var cart = cartRepository.getCartWithItems(cartId).orElseThrow(CartNotFoundException::new);

        cart.removeItem(variantId);
        cartRepository.save(cart);
    }

    public void clearCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        cart.clear();
        cartRepository.save(cart);
    }
}
