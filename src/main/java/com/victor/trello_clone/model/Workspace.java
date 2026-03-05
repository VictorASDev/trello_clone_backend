package com.victor.trello_clone.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
public class Workspace {
    @Id
    @Column(name = "workspace_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID workspaceId;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne()
    @JoinColumn(name = "owner_id")
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
        if (!(o instanceof Workspace workspace)) return false;
        return Objects.equals(getWorkspaceId(), workspace.getWorkspaceId()) && Objects.equals(getName(), workspace.getName()) && Objects.equals(getCreatedAt(), workspace.getCreatedAt()) && Objects.equals(getOwner(), workspace.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkspaceId(), getName(), getCreatedAt(), getOwner());
    }
}
