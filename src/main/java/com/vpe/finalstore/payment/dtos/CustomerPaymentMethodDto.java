package com.vpe.finalstore.payment.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerPaymentMethodDto {
    private Integer methodId;
    private Integer customerId;
    private String cardBrand;
    private String last4;
    private Integer expMonth;
    private Integer expYear;
    private Boolean isDefault;
}

