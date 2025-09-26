-- Composite keys have been clunky to deal with so adding surrogate keys to these tables.
-- This should simplify BE logic a lot


-- 1. product_image_assignments
ALTER TABLE product_image_assignments
    DROP FOREIGN KEY fk_product_image_assignments_product,
    DROP FOREIGN KEY fk_product_image_assignments_image;

ALTER TABLE product_image_assignments
    DROP PRIMARY KEY;

ALTER TABLE product_image_assignments
    ADD COLUMN assignment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

ALTER TABLE product_image_assignments
    ADD CONSTRAINT uq_product_image UNIQUE (product_id, image_id);


-- 2. product_variant_image_assignments
ALTER TABLE product_variant_image_assignments
    DROP FOREIGN KEY product_fk_variant_image_assignments_variant,
    DROP FOREIGN KEY product_fk_variant_image_assignments_image;

ALTER TABLE product_variant_image_assignments
    DROP PRIMARY KEY;

ALTER TABLE product_variant_image_assignments
    ADD COLUMN assignment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

-- keep uniqueness guarantee
ALTER TABLE product_variant_image_assignments
    ADD CONSTRAINT uq_variant_image UNIQUE (variant_id, image_id);
