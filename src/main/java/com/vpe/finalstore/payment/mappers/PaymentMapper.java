package com.vpe.finalstore.payment.mappers;

import com.vpe.finalstore.payment.dtos.PaymentDto;
import com.vpe.finalstore.payment.entities.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {
    public PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        var dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setInvoiceId(payment.getInvoice().getInvoiceId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setMethodName(payment.getMethod() != null ? payment.getMethod().getName() : null);
        dto.setStatus(payment.getStatus().getName());
        dto.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        dto.setStripeChargeId(payment.getStripeChargeId());
        dto.setStripeCustomerId(payment.getStripeCustomerId());

        return dto;
    }

    public List<PaymentDto> toDto(List<Payment> payments) {
        return payments.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}

