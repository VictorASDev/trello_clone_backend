CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    position BIGINT NOT NULL,
    list_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_cards_list
        FOREIGN KEY (list_id)
        REFERENCES lists(id)
        ON DELETE CASCADE
);