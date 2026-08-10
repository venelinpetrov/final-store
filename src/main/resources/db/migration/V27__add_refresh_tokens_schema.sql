-- Add MIN_ORDER_AMOUNT to
CREATE TABLE refresh_tokens
(
    refresh_token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL,
    token_hash       VARCHAR(255) NOT NULL,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at       DATETIME NOT NULL,
    revoked_at       DATETIME DEFAULT NULL
);