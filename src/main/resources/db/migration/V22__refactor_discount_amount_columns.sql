-- Rename discount to discount_amount in invoices table
ALTER TABLE invoices
CHANGE COLUMN discount discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00;

-- Move discount_amount column in orders table to be after tax column
ALTER TABLE orders
MODIFY COLUMN discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER tax;
