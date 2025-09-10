CREATE TABLE product_images
(
    image_id   INT          NOT NULL AUTO_INCREMENT,
    link       VARCHAR(255) NOT NULL UNIQUE,
    alt_text   VARCHAR(100) NOT NULL,
    PRIMARY KEY (image_id)
);

CREATE TABLE product_image_assignments
(
    product_id INT NOT NULL,
    image_id   INT NOT NULL,
    is_primary TINYINT DEFAULT 0,
    PRIMARY KEY (product_id, image_id),
    CONSTRAINT fk_product_image_assignments_product FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_image_assignments_image FOREIGN KEY (image_id) REFERENCES product_images (image_id) ON DELETE CASCADE
);

CREATE TABLE product_variant_image_assignments
(
    variant_id INT NOT NULL,
    image_id   INT NOT NULL,
    is_primary TINYINT DEFAULT 0,
    PRIMARY KEY (variant_id, image_id),
    CONSTRAINT product_fk_variant_image_assignments_variant FOREIGN KEY (variant_id) REFERENCES product_variants (variant_id) ON DELETE CASCADE,
    CONSTRAINT product_fk_variant_image_assignments_image FOREIGN KEY (image_id) REFERENCES product_images (image_id) ON DELETE CASCADE
);