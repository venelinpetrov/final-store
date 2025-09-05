package com.vpe.finalstore.brand.entities;

import com.vpe.finalstore.product.entities.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id", nullable = false)
    private Integer brandId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @OneToMany(mappedBy = "brand")
    private Set<Product> products = new LinkedHashSet<>();
}