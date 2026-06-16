package com.victor.trello_clone.data.dto;

import com.victor.trello_clone.model.workspace.Workspace;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkspaceDto {

    public WorkspaceDto() {
    }

    public WorkspaceDto(UUID workspaceId,
                        String name,
                        LocalDateTime createdAt,
                        Set<WorkspaceMemberDto> members) {
        this.workspaceId = workspaceId;
        this.name = name;
        this.createdAt = createdAt;
        this.members = members;
    }

    private UUID workspaceId;
    private String name;
    private LocalDateTime createdAt;
    private Set<WorkspaceMemberDto> members = new HashSet<>();

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

    public Set<WorkspaceMemberDto> getMembers() {
        return members;
    }

    public void setMembers(Set<WorkspaceMemberDto> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WorkspaceDto that)) return false;
        return Objects.equals(getWorkspaceId(), that.getWorkspaceId())
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getCreatedAt(), that.getCreatedAt())
                && Objects.equals(members, that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkspaceId(), getName(), getCreatedAt(), members);
    }

    public WorkspaceDto toDto(Workspace w) {
        return new WorkspaceDto(
                w.getWorkspaceId(),
                w.getName(),
                w.getCreatedAt(),
                w.getMembers()
                        .stream()
                        .map(member -> new WorkspaceMemberDto().toDto(member))
                        .collect(Collectors.toSet())
        );
    }
}
