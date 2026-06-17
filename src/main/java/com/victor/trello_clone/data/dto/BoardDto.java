package com.victor.trello_clone.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.victor.trello_clone.model.board.Board;
import com.victor.trello_clone.model.workspace.Workspace;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class BoardDto {

    public BoardDto(UUID id,
                    String name,
                    String description,
                    String backgroundColor,
                    LocalDateTime createdAt,
                    LocalDateTime updatedAt,
                    Workspace workspace) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.backgroundColor = backgroundColor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.workspace = workspace;
    }

    public BoardDto() {
    }

    private UUID id;
    private String name;
    private String description;
    private String backgroundColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Workspace workspace;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BoardDto board)) return false;
        return Objects.equals(getId(), board.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public BoardDto toDto(Board b)  {
        return new BoardDto(
                b.getId(),
                b.getName(),
                b.getDescription(),
                b.getBackgroundColor(),
                b.getCreatedAt(),
                b.getUpdatedAt(),
                b.getWorkspace()
        );
    }
}