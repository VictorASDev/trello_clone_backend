INSERT INTO workspace_member (
    workspace_id,
    user_id,
    role,
    joined_at
)
SELECT
    w.workspace_id,
    w.owner_id,
    'ADMIN',
    CURRENT_TIMESTAMP
FROM workspaces w
WHERE NOT EXISTS (
    SELECT 1
    FROM workspace_member wm
    WHERE wm.workspace_id = w.workspace_id
      AND wm.user_id = w.owner_id
);

ALTER TABLE workspaces
    DROP COLUMN owner_id;
