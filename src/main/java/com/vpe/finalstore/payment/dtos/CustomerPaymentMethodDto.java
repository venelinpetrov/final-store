package com.vpe.finalstore.payment.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerPaymentMethodDto {
    private Integer methodId;
    private Integer customerId;
    private String stripeMethodId;
    private String cardBrand;
    private String last4;
    private Integer expMonth;
    private Integer expYear;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

