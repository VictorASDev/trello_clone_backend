CREATE TABLE IF NOT EXISTS workspace_member (
    workspace_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP,

    PRIMARY KEY (workspace_id, user_id),

    CONSTRAINT fk_workspace_member_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspaces(workspace_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_workspace_member_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);