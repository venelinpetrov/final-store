-- Add tax breakdown fields to orders table
ALTER TABLE orders
    ADD COLUMN tax_breakdown             JSON         NULL COMMENT 'Detailed tax breakdown from Stripe Tax API with multiple jurisdictions',
    ADD COLUMN stripe_tax_calculation_id VARCHAR(255) NULL COMMENT 'Stripe Tax Calculation ID for reference and audit';

-- Add index for Stripe Tax Calculation ID lookups
CREATE INDEX idx_orders_stripe_tax_calculation_id ON orders (stripe_tax_calculation_id);

-- Add tax breakdown field to invoices table
ALTER TABLE invoices
    ADD COLUMN tax_breakdown JSON NULL COMMENT 'Detailed tax breakdown copied from order';

-- Add tax_code to products table for Stripe Tax API classification
-- Tax codes help Stripe determine correct tax rates for different product types
-- See: https://stripe.com/docs/tax/tax-codes
-- Common examples:
--   txcd_99999999 = General - Tangible Goods
--   txcd_10000000 = Digital Products
--   txcd_20030000 = Clothing
ALTER TABLE products
    ADD COLUMN tax_code VARCHAR(20) NULL DEFAULT 'txcd_99999999' COMMENT 'Stripe Tax Code for product classification';

-- Update existing products to use the general tangible goods tax code
UPDATE products SET tax_code = 'txcd_99999999' WHERE tax_code IS NULL;
