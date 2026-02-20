package com.vpe.finalstore.invoice.entities;

import com.vpe.finalstore.customer.entities.Customer;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.payment.entities.Payment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer invoiceId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @Column(name = "invoice_total")
    private BigDecimal invoiceTotal;

    @NotNull
    @Column(name = "tax")
    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull
    @Column(name = "discount")
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "payment_total")
    private BigDecimal paymentTotal = BigDecimal.ZERO;

    @Column(name = "invoice_date", insertable = false, updatable = false)
    private LocalDateTime invoiceDate;

    @NotNull
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @OneToMany(mappedBy = "invoice", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Payment> payments = new LinkedHashSet<>();
}

