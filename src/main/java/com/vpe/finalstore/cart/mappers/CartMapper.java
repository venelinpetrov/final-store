package com.vpe.finalstore.cart.mappers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemDto;
import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);

    @Mapping(source = "variant.variantId", target = "variantId")
    @Mapping(source = "itemId", target = "itemId")
    CartItemDto toDto(CartItem item);
}
