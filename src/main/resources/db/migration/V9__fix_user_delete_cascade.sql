ALTER TABLE address
DROP FOREIGN KEY fk_address_user;

ALTER TABLE address
    ADD CONSTRAINT fk_address_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE email_verification_tokens
DROP FOREIGN KEY fk_email_tokens_user;

ALTER TABLE email_verification_tokens
    ADD CONSTRAINT fk_email_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE password_verification_tokens
DROP FOREIGN KEY fk_password_tokens_user;

ALTER TABLE password_verification_tokens
    ADD CONSTRAINT fk_password_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE orders
DROP FOREIGN KEY fk_orders_user;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE reviews
DROP FOREIGN KEY reviews_ibfk_1;

ALTER TABLE reviews
    ADD CONSTRAINT reviews_ibfk_1
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE comments
DROP FOREIGN KEY comments_ibfk_1;

ALTER TABLE comments
    ADD CONSTRAINT comments_ibfk_1
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;


ALTER TABLE favorites
DROP FOREIGN KEY fk_favorites_user;

ALTER TABLE favorites
    ADD CONSTRAINT fk_favorites_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;