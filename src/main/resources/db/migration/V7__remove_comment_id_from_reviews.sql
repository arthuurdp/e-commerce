ALTER TABLE reviews
DROP FOREIGN KEY fk_reviews_comment,
    DROP COLUMN comment_id;