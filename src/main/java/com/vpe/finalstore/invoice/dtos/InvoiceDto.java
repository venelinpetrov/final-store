package com.vpe.finalstore.invoice.dtos;

import com.vpe.finalstore.invoice.enums.InvoiceStatusType;
import com.vpe.finalstore.payment.dtos.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InvoiceDto {
    private Integer invoiceId;
    private InvoiceStatusType status;
    private Integer orderId;
    private Integer customerId;
    private BigDecimal invoiceTotal;
    private BigDecimal tax;
    private BigDecimal discountAmount;
    private BigDecimal paymentTotal;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private List<PaymentDto> payments;
}

