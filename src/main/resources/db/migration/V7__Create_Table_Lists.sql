CREATE TABLE lists (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    position BIGINT NOT NULL,
    board_id UUID NOT NULL,

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_lists_board
        FOREIGN KEY (board_id)
        REFERENCES boards(board_id)
        ON DELETE CASCADE
);

