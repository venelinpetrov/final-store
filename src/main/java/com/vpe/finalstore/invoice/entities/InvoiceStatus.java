package com.vpe.finalstore.invoice.entities;

import com.vpe.finalstore.invoice.enums.InvoiceStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoice_statuses")
public class InvoiceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", unique = true)
    private InvoiceStatusType name;
}

