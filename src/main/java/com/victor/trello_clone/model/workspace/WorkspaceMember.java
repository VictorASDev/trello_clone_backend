package com.victor.trello_clone.model.workspace;

import com.victor.trello_clone.model.user.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

import java.time.Instant;
import java.util.Objects;

@Entity
public class WorkspaceMember {

    public WorkspaceMember() {
    }

    public WorkspaceMember(
                           Workspace workspace,
                           User user,
                           String role,
                           Instant joinedAt) {
        this.workspace = workspace;
        this.user = user;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    @EmbeddedId
    private WorkspaceMemberId id;

    @ManyToOne
    @MapsId("workspaceId")
    private Workspace workspace;

    @ManyToOne
    @MapsId("userId")
    private User user;

    private String role;

    private Instant joinedAt;

    public WorkspaceMemberId getId() {
        return id;
    }

    public void setId(WorkspaceMemberId id) {
        this.id = id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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
        if (!(o instanceof WorkspaceMember that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getWorkspace(), that.getWorkspace()) && Objects.equals(getUser(), that.getUser()) && Objects.equals(getRole(), that.getRole()) && Objects.equals(getJoinedAt(), that.getJoinedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWorkspace(), getUser(), getRole(), getJoinedAt());
    }
}