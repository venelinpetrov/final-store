package com.vpe.finalstore.payment.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentIntentCreateDto {
    @NotNull(message = "Cart ID is required")
    private UUID cartId;

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    @NotNull(message = "Address ID is required")
    private Integer addressId;
}

