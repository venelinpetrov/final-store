-- Add postal_code and country_code to customer_addresses table
ALTER TABLE customer_addresses
    ADD COLUMN postal_code VARCHAR(20) DEFAULT NULL AFTER street,
    ADD COLUMN country_code VARCHAR(2) DEFAULT NULL AFTER country;
