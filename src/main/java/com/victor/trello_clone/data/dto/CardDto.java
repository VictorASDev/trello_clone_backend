package com.victor.trello_clone.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.victor.trello_clone.model.boardList.BoardList;
import com.victor.trello_clone.model.card.Card;
import java.time.LocalDateTime;
import java.util.Objects;

public class CardDto {

    private Long id;
    private String title;
    private String description;
    private Long position;
    @JsonIgnore
    private BoardList list;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public BoardList getList() {
        return list;
    }

    public void setList(BoardList list) {
        this.list = list;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CardDto cardDto)) return false;
        return Objects.equals(getId(), cardDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public static CardDto toDto(Card card) {
        if (card == null)
            return null;

        var dto = new CardDto();

        dto.setTitle(card.getTitle());
        dto.setList(card.getList());
        dto.setId(card.getId());
        dto.setDescription(card.getDescription());
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());
        dto.setPosition(card.getPosition());

        return dto;
    }
}
