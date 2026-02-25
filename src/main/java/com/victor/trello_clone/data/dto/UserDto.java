package com.victor.trello_clone.data.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.victor.trello_clone.model.enums.Role;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@JsonPropertyOrder({"id", "email", "username", "createdAt", "updatedAt", "roles"})
public class UserDto {

    private UUID id;
    private String email;
    private String username;
    private Set<Role> roles = new HashSet<>();
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
        return Objects.equals(getId(), userDto.getId()) && Objects.equals(getEmail(), userDto.getEmail()) && Objects.equals(getUsername(), userDto.getUsername()) && Objects.equals(getRoles(), userDto.getRoles()) && Objects.equals(getCreatedAt(), userDto.getCreatedAt()) && Objects.equals(getUpdatedAt(), userDto.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getUsername(), getRoles(), getCreatedAt(), getUpdatedAt());
    }
}
