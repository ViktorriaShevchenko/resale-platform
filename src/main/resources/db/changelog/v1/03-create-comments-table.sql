-- liquibase formatted sql

-- changeset author:3
CREATE TABLE comments (
    pk SERIAL PRIMARY KEY,
    text VARCHAR(64) NOT NULL,
    created_at BIGINT NOT NULL,
    author_id INTEGER NOT NULL,
    ad_id INTEGER NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (ad_id) REFERENCES ads(pk)
);