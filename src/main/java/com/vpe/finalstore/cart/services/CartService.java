package com.vpe.finalstore.cart.services;

import com.vpe.finalstore.auth.config.AuthService;
import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemDto;
import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.exceptions.CartNotFoundException;
import com.vpe.finalstore.cart.mappers.CartMapper;
import com.vpe.finalstore.cart.repositories.CartRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductVariantRepository variantRepository;
    private final CustomerRepository customerRepository;
    private final AuthService authService;
    private final CartMapper cartMapper;

    public Optional<CartDto> getCartWithItems(UUID cartId) {
        return cartRepository.getCartWithItems(cartId)
            .map(cartMapper::toDto);
    }

    public CartDto createCart() {
        var cart = new Cart();
        var user = authService.getCurrentuser();
        if (user != null) {
            var customer = user.getCustomer();
            cart.setCustomer(customer);
        }
        var savedCart = cartRepository.save(cart);
        return cartMapper.toDto(savedCart);
    }

    public CartItemDto addToCart(UUID cartId, Integer variantId) {
        var cart = cartRepository.getCartWithItems(cartId)
            .orElseThrow(CartNotFoundException::new);
        var variant = variantRepository.findByVariantId(variantId)
            .orElseThrow(VariantNotFoundException::new);

        var cartItem = cart.addItem(variant);

        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

    public CartItemDto updateCartItem(UUID cartId, Integer variantId, Integer quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        var cartItem = cart.getItem(variantId);

        if (cartItem == null) {
            throw new VariantNotFoundException();
        }

        cartItem.setQuantity(quantity);

        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
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

    public Optional<CartDto> getCartBySessionId(UUID sessionId) {
        return cartRepository.findBySessionId(sessionId)
            .map(cartMapper::toDto);
    }

    public Optional<CartDto> getCartByCustomerId(Integer customerId) {
        return cartRepository.findByCustomer_CustomerId(customerId)
            .map(cartMapper::toDto);
    }

    @Transactional
    public CartDto associateCartWithCustomer(UUID sessionId, Integer customerId) {
        var cart = cartRepository.findBySessionId(sessionId)
            .orElseThrow(() -> new NotFoundException("Cart not found"));

        var customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Customer not found"));

        // Check if customer already has a cart
        var existingCart = cartRepository.findByCustomer_CustomerId(customerId);

        Cart resultCart;
        if (existingCart.isPresent()) {
            // Merge: move items from anonymous cart to customer's cart
            for (var item : cart.getCartItems()) {
                existingCart.get().addItem(item.getVariant());
            }
            cartRepository.delete(cart); // Delete anonymous cart
            resultCart = cartRepository.save(existingCart.get());
        } else {
            // Simply associate the cart with the customer
            cart.setCustomer(customer);
            resultCart = cartRepository.save(cart);
        }

        return cartMapper.toDto(resultCart);
    }
}
