package com.vpe.finalstore.cart.mappers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemDto;
import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.entities.CartItem;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);

    CartItemDto toDto(CartItem item);
}
