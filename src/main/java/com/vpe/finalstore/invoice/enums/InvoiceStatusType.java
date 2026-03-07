package com.vpe.finalstore.invoice.enums;

public enum InvoiceStatusType {
    ISSUED,      // Invoice created and sent to customer
    PAID,        // Invoice fully paid
    REFUNDED,    // Payment refunded to customer
    CANCELLED,   // Order cancelled by customer
    VOID,        // Invoice voided (won't be paid)
    OVERDUE      // Invoice past due date without payment
}

