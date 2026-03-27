package com.vpe.finalstore.customer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "address_types")
public class AddressType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_type_id")
    private Integer addressTypeId;

    @NotNull
    @Column(name = "type_name")
    private String typeName;

}