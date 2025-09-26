-- This multistep migration denormalizes and backfills data from products and product_variants into order_items
-- so that changes in common fields don't cause changes in historical data. SKU won't change,
-- but it's convenient to denormalize it too.


-- Step 1: Add columns as nullable
ALTER TABLE order_items
    ADD COLUMN product_name VARCHAR(255) NULL,
    ADD COLUMN sku VARCHAR(20) NULL,
    ADD COLUMN brand_name VARCHAR(100) NULL;

-- Step 2: Backfill existing data
UPDATE order_items oi
    JOIN product_variants v ON oi.variant_id = v.variant_id
    JOIN products p ON v.product_id = p.product_id
    LEFT JOIN brands b ON p.brand_id = b.brand_id
    SET
        oi.product_name = p.name,
        oi.sku = v.sku,
        oi.brand_name = b.name;

-- Step 3: Enforce NOT NULL constraints
ALTER TABLE order_items
    MODIFY product_name VARCHAR(255) NOT NULL,
    MODIFY sku VARCHAR(20) NOT NULL,
    MODIFY brand_name VARCHAR(100) NOT NULL;
