-- Add payment tracking to payments table
ALTER TABLE payments
ADD COLUMN attempt_number INT NOT NULL DEFAULT 1,
ADD COLUMN failure_reason VARCHAR(255),
ADD COLUMN failure_code VARCHAR(100);

CREATE INDEX idx_payments_invoice_attempt ON payments(invoice_id, attempt_number);