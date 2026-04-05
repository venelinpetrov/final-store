-- Add product variant level discount schema

ALTER TABLE order_items
    ADD COLUMN discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER unit_price;

-- Track active product discounts
CREATE TABLE product_discounts (
    discount_id INT AUTO_INCREMENT PRIMARY KEY,
    variant_id INT NOT NULL,
    discount_percentage DECIMAL(5, 2) NOT NULL,
    valid_from DATETIME NOT NULL,
    valid_until DATETIME NOT NULL,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id)
);