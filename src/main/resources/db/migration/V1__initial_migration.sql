-- Customer / User model

CREATE TABLE users
(
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    is_active     BOOLEAN  DEFAULT TRUE,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles
(
    role_id     INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE user_roles
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);

CREATE TABLE customers
(
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT UNIQUE,
    name          VARCHAR(100) NOT NULL,
    phone         VARCHAR(30)  NOT NULL,
    date_of_birth DATE,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE address_types
(
    address_type_id INT         NOT NULL AUTO_INCREMENT,
    type_name       VARCHAR(50) NOT NULL,
    PRIMARY KEY (address_type_id),
    UNIQUE KEY idx_address_types_type_name_UNIQUE (type_name)
) ENGINE = InnoDB;

CREATE TABLE customer_addresses
(
    address_id      INT          NOT NULL AUTO_INCREMENT,
    customer_id     INT          NOT NULL,
    country         VARCHAR(100) NOT NULL,
    state           VARCHAR(100) DEFAULT NULL,
    city            VARCHAR(100) NOT NULL,
    street          VARCHAR(100) NOT NULL,
    floor           VARCHAR(10)  DEFAULT NULL,
    apartment_no    VARCHAR(10)  DEFAULT NULL,
    address_type_id INT          NOT NULL,
    is_default      TINYINT      DEFAULT 0,
    PRIMARY KEY (address_id),
    KEY fk_idx_customer_addresses_customer_id (customer_id),
    KEY fk_idx_customer_addresses_address_type_id (address_type_id),
    CONSTRAINT fk_customer_addresses_address_types FOREIGN KEY (address_type_id) REFERENCES address_types (address_type_id),
    CONSTRAINT fk_customers_customer_addresses FOREIGN KEY (customer_id) REFERENCES customers (customer_id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- Products model

CREATE TABLE product_categories
(
    category_id        INT          NOT NULL AUTO_INCREMENT,
    name               VARCHAR(100) NOT NULL,
    parent_category_id INT          NULL,
    PRIMARY KEY (category_id),
    UNIQUE KEY idx_product_categories_name_UNIQUE (name),
    KEY fk_idx_product_categories_parent_category_id (parent_category_id),
    FOREIGN KEY (parent_category_id) REFERENCES product_categories (category_id) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE brands
(
    brand_id INT          NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    logo_url VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (brand_id),
    UNIQUE KEY idx_brands_name_UNIQUE (name)
) ENGINE = InnoDB;

CREATE TABLE products
(
    product_id  INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    brand_id    INT                   DEFAULT NULL,
    is_archived TINYINT      NOT NULL DEFAULT 0,
    created_at  DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    KEY idx_products_name (name),
    KEY fk_idx_products_brand_id (brand_id),
    CONSTRAINT fk_products_brands FOREIGN KEY (brand_id) REFERENCES brands (brand_id) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE tags
(
    tag_id INT         NOT NULL AUTO_INCREMENT,
    name   VARCHAR(50) NOT NULL,
    PRIMARY KEY (tag_id),
    UNIQUE KEY idx_tags_name_UNIQUE (name)
) ENGINE = InnoDB;

CREATE TABLE product_tags
(
    product_id INT NOT NULL,
    tag_id     INT NOT NULL,
    PRIMARY KEY (product_id, tag_id),
    KEY fk_idx_product_tags_product_id (product_id),
    KEY fk_idx_product_tags_tag_id (tag_id),
    CONSTRAINT fk_product_tags_product FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE product_to_categories
(
    product_id  INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    KEY fk_idx_product_to_categories_product_id (product_id),
    KEY fk_idx_product_to_categories_category_id (category_id),
    FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES product_categories (category_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE product_variants
(
    variant_id  INT            NOT NULL AUTO_INCREMENT,
    product_id  INT            NOT NULL,
    sku         VARCHAR(20)    NOT NULL,
    unit_price  DECIMAL(10, 2) NOT NULL,
    is_archived TINYINT        NOT NULL DEFAULT 0,
    created_at  DATETIME                DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (variant_id),
    UNIQUE KEY idx_product_variants_sku_UNIQUE (sku),
    KEY fk_idx_product_variants_product_id (product_id),
    CONSTRAINT fk_product_variants_products FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE variant_options
(
    option_id INT         NOT NULL AUTO_INCREMENT,
    name      VARCHAR(50) NOT NULL,
    PRIMARY KEY (option_id),
    UNIQUE KEY idx_variant_options_name_UNIQUE (name)
) ENGINE = InnoDB;

CREATE TABLE variant_option_values
(
    value_id  INT         NOT NULL AUTO_INCREMENT,
    option_id INT         NOT NULL,
    value     VARCHAR(50) NOT NULL,
    PRIMARY KEY (value_id),
    KEY fk_idx_variant_option_values_option_id (option_id),
    UNIQUE KEY idx_variant_option_values_option_value_UNIQUE (option_id, value),
    CONSTRAINT fk_variant_option_values_variant_options FOREIGN KEY (option_id) REFERENCES variant_options (option_id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE variant_option_assignments
(
    variant_id INT NOT NULL,
    value_id   INT NOT NULL,
    PRIMARY KEY (variant_id, value_id),
    KEY fk_idx_variant_option_assignments_variant_id (variant_id),
    KEY fk_idx_variant_option_assignments_value_id (value_id),
    FOREIGN KEY (variant_id) REFERENCES product_variants (variant_id) ON DELETE CASCADE,
    FOREIGN KEY (value_id) REFERENCES variant_option_values (value_id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE product_images
(
    image_id INT          NOT NULL AUTO_INCREMENT,
    link     VARCHAR(255) NOT NULL UNIQUE,
    alt_text VARCHAR(100) NOT NULL,
    PRIMARY KEY (image_id)
);

CREATE TABLE product_image_assignments
(
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id    INT NOT NULL,
    image_id      INT NOT NULL,
    is_primary    TINYINT DEFAULT 0,
    CONSTRAINT uq_product_image UNIQUE (product_id, image_id),
    CONSTRAINT fk_product_image_assignments_product FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_image_assignments_image FOREIGN KEY (image_id) REFERENCES product_images (image_id) ON DELETE CASCADE
);

CREATE TABLE product_variant_image_assignments
(
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    variant_id    INT NOT NULL,
    image_id      INT NOT NULL,
    is_primary    TINYINT DEFAULT 0,
    CONSTRAINT uq_variant_image UNIQUE (variant_id, image_id),
    CONSTRAINT product_fk_variant_image_assignments_variant FOREIGN KEY (variant_id) REFERENCES product_variants (variant_id) ON DELETE CASCADE,
    CONSTRAINT product_fk_variant_image_assignments_image FOREIGN KEY (image_id) REFERENCES product_images (image_id) ON DELETE CASCADE
);

-- Cart

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
    CONSTRAINT fk_cart_items_variants FOREIGN KEY (variant_id) REFERENCES product_variants (variant_id) ON DELETE CASCADE
);

-- Orders and Shipment models

CREATE TABLE order_statuses
(
    status_id INT         NOT NULL AUTO_INCREMENT,
    name      VARCHAR(20) NOT NULL UNIQUE,
    PRIMARY KEY (status_id)
) ENGINE = InnoDB;

CREATE TABLE orders
(
    order_id    INT NOT NULL AUTO_INCREMENT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status_id   INT NOT NULL,
    customer_id INT NOT NULL,
    address_id  INT NOT NULL,
    PRIMARY KEY (order_id),
    KEY fk_idx_orders_status_id (status_id),
    KEY fk_idx_orders_customer_id (customer_id),
    KEY fk_idx_orders_address_id (address_id),
    KEY idx_orders_customer_status (customer_id, status_id),
    FOREIGN KEY (status_id) REFERENCES order_statuses (status_id),
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    FOREIGN KEY (address_id) REFERENCES customer_addresses (address_id)
) ENGINE = InnoDB;

CREATE TABLE order_items
(
    order_id     INT            NOT NULL,
    variant_id   INT            NOT NULL,
    quantity     INT            NOT NULL,
    product_name VARCHAR(255)   NULL,
    sku          VARCHAR(20)    NULL,
    brand_name   VARCHAR(100)   NULL,
    unit_price   DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (order_id, variant_id),
    KEY fk_idx_order_items_order_id (order_id),
    KEY fk_idx_order_items_variant_id (variant_id),
    FOREIGN KEY (order_id) REFERENCES orders (order_id),
    FOREIGN KEY (variant_id) REFERENCES product_variants (variant_id)
) ENGINE = InnoDB;


CREATE TABLE shipments
(
    shipment_id     INT         NOT NULL AUTO_INCREMENT,
    carrier         VARCHAR(50) NOT NULL,
    tracking_number VARCHAR(50) NOT NULL UNIQUE,
    shipment_date   DATETIME    NOT NULL,
    delivery_date   DATETIME DEFAULT NULL,
    order_id        INT         NOT NULL,
    address_id      INT         NOT NULL,
    PRIMARY KEY (shipment_id),
    KEY fk_idx_shipments_order_id (order_id),
    KEY fk_idx_shipments_address_id (address_id),
    FOREIGN KEY (order_id) REFERENCES orders (order_id),
    FOREIGN KEY (address_id) REFERENCES customer_addresses (address_id)
) ENGINE = InnoDB;

-- Invoices and Payments model

CREATE TABLE invoices
(
    invoice_id    INT            NOT NULL AUTO_INCREMENT,
    order_id      INT            NOT NULL,
    customer_id   INT            NOT NULL,
    invoice_total DECIMAL(10, 2) NOT NULL,              -- subtotal + shipping
    tax           DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount      DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- amount paid
    invoice_date  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date      DATETIME       NOT NULL,
    payment_date  DATETIME                DEFAULT NULL,
    PRIMARY KEY (invoice_id),
    KEY fk_idx_invoices_order_id (order_id),
    KEY fk_idx_invoices_customer_id (customer_id),
    FOREIGN KEY (order_id) REFERENCES orders (order_id),
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id)
) ENGINE = InnoDB;

CREATE TABLE payment_methods
(
    method_id INT                                                              NOT NULL AUTO_INCREMENT,
    type      ENUM ('Credit Card', 'Bank Transfer', 'PayPal', 'Cash', 'Other') NOT NULL,
    name      VARCHAR(50)                                                      NOT NULL,
    PRIMARY KEY (method_id),
    UNIQUE KEY idx_payment_methods_name_UNIQUE (name)
) ENGINE = InnoDB;

CREATE TABLE payment_statuses
(
    status_id INT         NOT NULL AUTO_INCREMENT,
    name      VARCHAR(20) NOT NULL,
    PRIMARY KEY (status_id),
    UNIQUE KEY idx_payment_statuses_name_UNIQUE (name)
) ENGINE = InnoDB;

CREATE TABLE payments
(
    payment_id   INT            NOT NULL AUTO_INCREMENT,
    invoice_id   INT            NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL,
    payment_date DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    method_id    INT,
    status_id    INT            NOT NULL,
    PRIMARY KEY (payment_id),
    KEY fk_idx_payments_invoice_id (invoice_id),
    KEY fk_idx_payments_method_id (method_id),
    KEY fk_idx_payments_status_id (status_id),
    FOREIGN KEY (invoice_id) REFERENCES invoices (invoice_id) ON DELETE CASCADE,
    FOREIGN KEY (method_id) REFERENCES payment_methods (method_id) ON DELETE SET NULL,
    FOREIGN KEY (status_id) REFERENCES payment_statuses (status_id)
) ENGINE = InnoDB;

CREATE TABLE inventory_levels
(
    inventory_id      INT NOT NULL AUTO_INCREMENT,
    variant_id        INT NOT NULL,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    created_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (inventory_id),
    UNIQUE KEY idx_inventory_variant_id_UNIQUE (variant_id),
    KEY fk_idx_inventory_variant_id (variant_id),
    CONSTRAINT fk_inventory_levels_variant FOREIGN KEY (variant_id)
        REFERENCES product_variants (variant_id)
        ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE inventory_movements
(
    movement_id   INT                              NOT NULL AUTO_INCREMENT,
    variant_id    INT                              NOT NULL,
    movement_type ENUM ('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    quantity      INT                              NOT NULL,
    reason        VARCHAR(100),
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (movement_id),
    KEY fk_idx_inventory_movements_variant_id (variant_id),
    CONSTRAINT fk_inventory_movements_variants FOREIGN KEY (variant_id)
        REFERENCES product_variants (variant_id)
        ON DELETE CASCADE
) ENGINE = InnoDB;

DELIMITER $$

CREATE TRIGGER trg_after_inventory_movement_insert
    AFTER INSERT
    ON inventory_movements
    FOR EACH ROW
BEGIN
    DECLARE updated_quantity INT;

    SET updated_quantity =
            CASE
                WHEN NEW.movement_type = 'IN' THEN NEW.quantity
                WHEN NEW.movement_type = 'OUT' THEN -NEW.quantity
                ELSE 0
                END;

    INSERT INTO inventory_levels (variant_id, quantity_in_stock)
    VALUES (NEW.variant_id, updated_quantity)
    ON DUPLICATE KEY UPDATE quantity_in_stock = quantity_in_stock + updated_quantity,
                            updated_at        = CURRENT_TIMESTAMP;
END$$

DELIMITER ;