package com.vpe.finalstore.product.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @Size(max = 255)
    @NotNull
    @Column(name = "link")
    private String link;

    @Size(max = 100)
    @NotNull
    @Column(name = "alt_text")
    private String altText;
}