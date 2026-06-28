package com.victor.trello_clone.data.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@JsonPropertyOrder({"id", "name", "backgroundColor", "lists"})
public class BoardViewDto {

    private UUID id;
    private String name;
    private String backgroundColor;

    private List<BoardListViewDto> lists;

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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public List<BoardListViewDto> getLists() {
        return lists;
    }

    public void setLists(List<BoardListViewDto> lists) {
        this.lists = lists;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BoardViewDto that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}