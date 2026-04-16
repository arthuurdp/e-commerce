ALTER TABLE favorites DROP FOREIGN KEY favorites_ibfk_1;
ALTER TABLE favorites DROP FOREIGN KEY favorites_ibfk_2;

ALTER TABLE favorites
    DROP PRIMARY KEY,
    ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST,
    CHANGE COLUMN added_at created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE favorites ADD CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE favorites ADD CONSTRAINT fk_favorites_product FOREIGN KEY (product_id) REFERENCES products(id);