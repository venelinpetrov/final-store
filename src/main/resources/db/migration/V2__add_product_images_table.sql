CREATE TABLE product_images
(
    image_id   INT          NOT NULL AUTO_INCREMENT,
    product_id INT          NULL,
    variant_id INT          NULL,
    link       VARCHAR(255) NOT NULL UNIQUE,
    alt_text   VARCHAR(100) NOT NULL,
    is_primary TINYINT DEFAULT 0,
    PRIMARY KEY (image_id),
    CONSTRAINT fk_product_images_products FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_images_variants FOREIGN KEY (variant_id) REFERENCES product_variants (variant_id) ON DELETE CASCADE
);