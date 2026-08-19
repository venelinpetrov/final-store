package com.vpe.finalstore.cart.mappers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemDto;
import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.entities.CartItem;
import com.vpe.finalstore.product.mappers.ProductVariantMapper;

import org.mapstruct.Mapper;


@Mapper(
    componentModel = "spring",
    uses = ProductVariantMapper.class
)
public interface CartMapper {
    CartDto toDto(Cart cart);

    CartItemDto toDto(CartItem item);
}
