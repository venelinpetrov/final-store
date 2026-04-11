-- Add discount amount column in orders schema

ALTER TABLE orders
    ADD COLUMN discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
