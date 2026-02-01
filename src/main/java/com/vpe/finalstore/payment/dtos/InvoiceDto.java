package com.vpe.finalstore.payment.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InvoiceDto {
    private Integer invoiceId;
    private Integer orderId;
    private Integer customerId;
    private BigDecimal invoiceTotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal paymentTotal;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private List<PaymentDto> payments;
}

