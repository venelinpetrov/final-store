CREATE TABLE carts
(
    cart_id     INT AUTO_INCREMENT PRIMARY KEY,
    session_id  VARCHAR(255) UNIQUE NULL,
    customer_id INT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY (customer_id),
    CONSTRAINT fk_carts_customers FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
);

CREATE TABLE cart_items
(
    item_id    INT AUTO_INCREMENT PRIMARY KEY,
    cart_id    INT NOT NULL,
    variant_id INT NOT NULL,
    quantity   INT NOT NULL DEFAULT 1,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (cart_id, variant_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (cart_id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product_variants FOREIGN KEY (variant_id) REFERENCES product_variants (variant_id) ON DELETE CASCADE
);