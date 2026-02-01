package com.vpe.finalstore.payment.mappers;

import com.vpe.finalstore.payment.dtos.InvoiceDto;
import com.vpe.finalstore.payment.entities.Invoice;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@AllArgsConstructor
@Component
public class InvoiceMapper {
    private final PaymentMapper paymentMapper;

    public InvoiceDto toDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        var dto = new InvoiceDto();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setOrderId(invoice.getOrder().getOrderId());
        dto.setCustomerId(invoice.getCustomer().getCustomerId());
        dto.setInvoiceTotal(invoice.getInvoiceTotal());
        dto.setTax(invoice.getTax());
        dto.setDiscount(invoice.getDiscount());
        dto.setPaymentTotal(invoice.getPaymentTotal());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setPaymentDate(invoice.getPaymentDate());
        dto.setPayments(invoice.getPayments() != null 
            ? paymentMapper.toDto(new ArrayList<>(invoice.getPayments())) 
            : null);

        return dto;
    }
}

