package com.vpe.finalstore.product.exceptions;

import com.vpe.finalstore.exceptions.NotFoundException;

public class VariantNotFoundException extends NotFoundException {
    public VariantNotFoundException() {
        super("Variant not found");
    }
}
