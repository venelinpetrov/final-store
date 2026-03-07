-- Remove attempt_number column from payments table. It's not used

ALTER TABLE payments DROP COLUMN attempt_number;

