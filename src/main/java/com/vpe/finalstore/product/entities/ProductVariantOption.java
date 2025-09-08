package com.vpe.finalstore.product.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "variant_options")
public class ProductVariantOption {
    @Id
    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "name")
    private String name;
}