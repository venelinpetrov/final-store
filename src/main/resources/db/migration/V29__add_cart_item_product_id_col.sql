-- Add product_id column

ALTER TABLE cart_items
    ADD COLUMN product_id INT NOT NULL DEFAULT 0;
