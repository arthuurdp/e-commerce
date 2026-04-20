ALTER TABLE order_items
DROP FOREIGN KEY fk_order_items_order;

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE;


ALTER TABLE payments
DROP FOREIGN KEY fk_payments_order;

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE;


ALTER TABLE shippings
DROP FOREIGN KEY fk_shippings_order;

ALTER TABLE shippings
    ADD CONSTRAINT fk_shippings_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE;


ALTER TABLE orders
DROP FOREIGN KEY fk_orders_address;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_address
        FOREIGN KEY (address_id)
            REFERENCES address(id)
            ON DELETE CASCADE;