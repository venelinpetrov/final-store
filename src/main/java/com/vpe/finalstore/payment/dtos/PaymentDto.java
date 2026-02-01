package com.vpe.finalstore.payment.dtos;

import com.vpe.finalstore.payment.enums.PaymentStatusType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDto {
    private Integer paymentId;
    private Integer invoiceId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String methodName;
    private PaymentStatusType status;
    private String stripePaymentIntentId;
    private String stripeChargeId;
    private String stripeCustomerId;
}

