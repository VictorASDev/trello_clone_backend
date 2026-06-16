package com.victor.trello_clone.model.workspace;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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

    @OneToMany(mappedBy = "workspace")
    private Set<WorkspaceMember> members = new HashSet<>();

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

    public Set<WorkspaceMember> getMembers() {
        return members;
    }

    public void setMembers(Set<WorkspaceMember> members) {
        this.members = members;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Workspace workspace)) return false;
        return Objects.equals(getWorkspaceId(), workspace.getWorkspaceId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getWorkspaceId());
    }
}
