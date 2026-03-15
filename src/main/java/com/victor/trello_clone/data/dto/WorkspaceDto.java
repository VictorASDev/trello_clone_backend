package com.victor.trello_clone.data.dto;

import com.victor.trello_clone.model.user.User;
import com.victor.trello_clone.model.workspace.Workspace;
import com.victor.trello_clone.model.workspace.WorkspaceMember;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class WorkspaceDto {

    public WorkspaceDto() {
    }

    public WorkspaceDto(UUID workspaceId,
                        String name,
                        LocalDateTime createdAt,
                        User owner,
                        Set<WorkspaceMember> members) {
        this.workspaceId = workspaceId;
        this.name = name;
        this.createdAt = createdAt;
        this.owner = owner;
        this.members = members;
    }

    private UUID workspaceId;
    private String name;
    private LocalDateTime createdAt;
    private User owner;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<WorkspaceMember> getMembers() {
        return members;
    }

    public void setMembers(Set<WorkspaceMember> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WorkspaceDto that)) return false;
        return Objects.equals(getWorkspaceId(), that.getWorkspaceId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getOwner(), that.getOwner()) && Objects.equals(getMembers(), that.getMembers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkspaceId(), getName(), getCreatedAt(), getOwner(), getMembers());
    }

    public WorkspaceDto toDto(Workspace w) {
        return new WorkspaceDto(
                w.getWorkspaceId(),
                w.getName(),
                w.getCreatedAt(),
                w.getOwner(),
                w.getMembers()
        );
    }
}
