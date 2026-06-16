package com.victor.trello_clone.model.workspace;

import com.victor.trello_clone.model.board.Board;
import com.victor.trello_clone.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

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

    @OneToMany(mappedBy = "workspace")
    private List<Board> boards;

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

    public List<Board> getBoards() {
        return boards;
    }

    public void setBoards(List<Board> boards) {
        this.boards = boards;
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
