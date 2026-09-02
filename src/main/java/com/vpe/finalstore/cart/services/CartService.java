package com.vpe.finalstore.cart.services;

import com.vpe.finalstore.auth.config.AuthService;
import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemDto;
import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.exceptions.CartNotFoundException;
import com.vpe.finalstore.cart.mappers.CartMapper;
import com.vpe.finalstore.cart.repositories.CartRepository;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;
import com.vpe.finalstore.users.repositories.UserRepository;

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
    private final AuthService authService;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;

    public Optional<CartDto> getCartWithItems(UUID cartId) {
        return cartRepository.getCartWithItems(cartId)
            .map(cartMapper::toDto);
    }

    public CartDto getOrCreateCart(String sessionId) {
        var user = authService.getCurrentUser();

        // Authenticated user
        if (user != null) {
            var customer = user.getCustomer();

            if (customer == null) {
                throw new NotFoundException("Customer not found");
            }

            return cartRepository
                .findByCustomer_CustomerId(customer.getCustomerId())
                .map(cartMapper::toDto)
                .orElseGet(() -> {
                    var cart = new Cart();
                    cart.setCustomer(customer);

                    return cartMapper.toDto(
                        cartRepository.save(cart)
                    );
                });
        }

        // Anonymous user
        if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository
                .findBySessionId(UUID.fromString(sessionId))
                .map(cartMapper::toDto)
                .orElseThrow(CartNotFoundException::new);
        }

        // Anonymous user without a cart
        var cart = cartRepository.save(new Cart());

        return cartMapper.toDto(cart);
    }

    private Cart getCart(String sessionId) {
        var user = authService.getCurrentUser();

        if (user != null) {
            var customer = user.getCustomer();

            if (customer == null) {
                throw new NotFoundException("Customer not found");
            }

            return cartRepository
                .findByCustomer_CustomerId(customer.getCustomerId())
                // .map(cartMapper::toDto)
                .orElse(null);
        }

        if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository
                .findBySessionId(UUID.fromString(sessionId))
                // .map(cartMapper::toDto)
                .orElseThrow(CartNotFoundException::new);
        }

        return null;
    }

    public CartDto getCartDto(String sessionId) {
        return cartMapper.toDto( getCart(sessionId));

    }

    public void updateCart(String sessionId, Integer variantId, Integer quantity) {
        var cart = getCart(sessionId);

        if (cart == null) {
            throw new CartNotFoundException();
        }

        var variant = variantRepository.findByVariantId(variantId)
            .orElseThrow(VariantNotFoundException::new);

        cart.addItem(variant, quantity);

        cartRepository.save(cart);
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
    public void associateCartWithCustomer(Integer userId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }

        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        var customer = user.getCustomer();

        if (customer == null) {
            throw new NotFoundException("Customer not found");
        }

        var customerId = customer.getCustomerId();

        UUID sessionUuid;

        try {
            sessionUuid = UUID.fromString(sessionId);
        } catch (IllegalArgumentException e) {
            throw new CartNotFoundException();
        }

        var anonymousCart = cartRepository.findBySessionId(sessionUuid)
            .orElseThrow(CartNotFoundException::new);

        var existingCart = cartRepository.findByCustomer_CustomerId(customerId);

        if (existingCart.isPresent()) {
            var customerCart = existingCart.get();

            // Merge anonymous cart items into customer cart
            for (var item : anonymousCart.getCartItems()) {
                customerCart.addItem(item.getVariant(), item.getQuantity());
            }

            cartRepository.delete(anonymousCart);
        } else {
            // No existing customer cart, so associate anonymous cart
            anonymousCart.setCustomer(customer);
        }
    }
}
