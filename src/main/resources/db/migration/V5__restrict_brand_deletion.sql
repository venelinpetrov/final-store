ALTER TABLE products DROP FOREIGN KEY fk_products_brands;

ALTER TABLE products
    ADD CONSTRAINT fk_products_brands
    FOREIGN KEY (brand_id)
    REFERENCES brands (brand_id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;