package com.victor.trello_clone.data.dto;

import com.victor.trello_clone.model.workspace.WorkspaceMember;
import com.victor.trello_clone.model.workspace.WorkspaceRole;

import java.time.Instant;
import java.util.Objects;

public class WorkspaceMemberDto {

    public WorkspaceMemberDto() {
    }

    public WorkspaceMemberDto(UserDto user, WorkspaceRole role, Instant joinedAt) {
        this.user = user;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    private UserDto user;
    private WorkspaceRole role;
    private Instant joinedAt;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public WorkspaceRole getRole() {
        return role;
    }

    public void setRole(WorkspaceRole role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WorkspaceMemberDto that)) return false;
        return Objects.equals(getUser(), that.getUser())
                && getRole() == that.getRole()
                && Objects.equals(getJoinedAt(), that.getJoinedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getRole(), getJoinedAt());
    }

    public WorkspaceMemberDto toDto(WorkspaceMember member) {
        return new WorkspaceMemberDto(
                new UserDto().toDto(member.getUser()),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
