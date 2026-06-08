package com.victor.trello_clone.data.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.victor.trello_clone.model.user.User;

import java.time.LocalDateTime;

import java.util.Objects;
import java.util.UUID;

@JsonPropertyOrder({"id", "email", "username", "createdAt", "updatedAt", "roles"})
public class UserDto {

    public UserDto(UUID id, String email, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserDto() {
    }

    private UUID id;
    private String email;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserDto userDto)) return false;
        return Objects.equals(getId(), userDto.getId()) && Objects.equals(getEmail(), userDto.getEmail()) && Objects.equals(getUsername(), userDto.getUsername()) && Objects.equals(getCreatedAt(), userDto.getCreatedAt()) && Objects.equals(getUpdatedAt(), userDto.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getUsername(), getCreatedAt(), getUpdatedAt());
    }

    public UserDto toDto(User u) {
        return new UserDto(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
