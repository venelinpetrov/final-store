package com.vpe.finalstore.payment.entities;

import com.vpe.finalstore.payment.enums.PaymentStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment_statuses")
public class PaymentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private PaymentStatusType name;
}

