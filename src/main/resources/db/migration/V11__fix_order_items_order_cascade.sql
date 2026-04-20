ALTER TABLE order_items
DROP FOREIGN KEY fk_order_items_order;

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE;