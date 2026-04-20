ALTER TABLE orders
DROP FOREIGN KEY fk_orders_address;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_address
        FOREIGN KEY (address_id)
            REFERENCES address(id)
            ON DELETE CASCADE;