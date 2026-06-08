CREATE TABLE IF NOT EXISTS workspaces  (
    workspace_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,

    FOREIGN KEY (owner_id) REFERENCES users(id)
)