package com.vpe.finalstore.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PaymentIntentResponseDto {
    private String clientSecret;
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
}

