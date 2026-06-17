CREATE TABLE boards (
    board_id UUID NOT NULL,

    name VARCHAR(255) NOT NULL,
    description TEXT,
    background_color VARCHAR(255),

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    workspace_id UUID,

    CONSTRAINT pk_boards
        PRIMARY KEY (board_id),

    CONSTRAINT fk_boards_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspaces(workspace_id)
);