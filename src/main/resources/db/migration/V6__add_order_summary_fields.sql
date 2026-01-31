-- Add financial summary fields to orders table
ALTER TABLE orders
    ADD COLUMN subtotal      DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN tax           DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN shipping_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN total         DECIMAL(10, 2) NOT NULL DEFAULT 0.00;

