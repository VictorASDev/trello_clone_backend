package com.victor.trello_clone.data.dto;

import com.victor.trello_clone.model.User;
import com.victor.trello_clone.model.Workspace;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class WorkspaceDto {

    public WorkspaceDto() {
    }

    public WorkspaceDto(UUID workspaceId, String name, LocalDateTime createdAt, User owner) {
        this.workspaceId = workspaceId;
        this.name = name;
        this.createdAt = createdAt;
        this.owner = owner;
    }

    private UUID workspaceId;
    private String name;
    private LocalDateTime createdAt;
    private User owner;

    public UUID getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WorkspaceDto workspace)) return false;
        return Objects.equals(getWorkspaceId(), workspace.getWorkspaceId()) && Objects.equals(getName(), workspace.getName()) && Objects.equals(getCreatedAt(), workspace.getCreatedAt()) && Objects.equals(getOwner(), workspace.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkspaceId(), getName(), getCreatedAt(), getOwner());
    }

    public WorkspaceDto toDto(Workspace w) {
        return new WorkspaceDto(
                w.getWorkspaceId(),
                w.getName(),
                w.getCreatedAt(),
                w.getOwner()
        );
    }
}
