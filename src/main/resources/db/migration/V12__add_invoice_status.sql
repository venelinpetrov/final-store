-- Create invoice_statuses table
CREATE TABLE invoice_statuses (
    status_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (status_id)
);

-- Insert invoice status types
INSERT INTO
    invoice_statuses (name)
VALUES
    ('ISSUED'),
    ('PAID'),
    ('REFUNDED'),
    ('CANCELLED'),
    ('VOID'),
    ('OVERDUE');

-- Add status_id column to invoices table
ALTER TABLE
    invoices
ADD
    COLUMN status_id INT NULL
AFTER
    invoice_id;

-- Set default status to ISSUED for existing invoices
-- Determine status based on payment_date:
-- - If payment_date is set -> PAID
-- - If payment_date is null -> ISSUED
UPDATE
    invoices
SET
    status_id = (
        SELECT
            status_id
        FROM
            invoice_statuses
        WHERE
            name = 'PAID'
    )
WHERE
    payment_date IS NOT NULL;

UPDATE
    invoices
SET
    status_id = (
        SELECT
            status_id
        FROM
            invoice_statuses
        WHERE
            name = 'ISSUED'
    )
WHERE
    payment_date IS NULL;

-- Make status_id NOT NULL after setting values
ALTER TABLE
    invoices
MODIFY
    COLUMN status_id INT NOT NULL;

-- Add foreign key constraint
ALTER TABLE
    invoices
ADD
    CONSTRAINT fk_invoices_status_id FOREIGN KEY (status_id) REFERENCES invoice_statuses (status_id);

-- Add index for better query performance
CREATE INDEX idx_invoices_status_id ON invoices (status_id);