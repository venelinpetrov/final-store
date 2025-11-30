package com.vpe.finalstore.cart.exceptions;

import com.vpe.finalstore.exceptions.NotFoundException;

public class CartNotFoundException extends NotFoundException {
    public CartNotFoundException() {
        super("Cart not found");
    }
}
