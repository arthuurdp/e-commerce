ALTER TABLE comments
ADD COLUMN review_id BIGINT NOT NULL UNIQUE,
ADD CONSTRAINT fk_comments_review FOREIGN KEY (review_id) REFERENCES reviews(id);