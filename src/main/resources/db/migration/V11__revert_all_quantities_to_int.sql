-- Revert all quantity fields from SMALLINT to INT for consistency and simplicity

ALTER TABLE cart_items
    MODIFY quantity INT UNSIGNED NOT NULL DEFAULT 1;

ALTER TABLE order_items
    MODIFY quantity INT UNSIGNED NOT NULL;

ALTER TABLE inventory_movements
    MODIFY quantity INT UNSIGNED NOT NULL;

