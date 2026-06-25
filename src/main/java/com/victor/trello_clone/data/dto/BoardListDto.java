package com.victor.trello_clone.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.victor.trello_clone.model.board.Board;
import com.victor.trello_clone.model.boardList.BoardList;

import java.time.LocalDateTime;
import java.util.Objects;

public class BoardListDto {

    private Long id;
    private String title;
    private Long position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    private BoardDto board;
    public BoardListDto() {
    }

    public static BoardListDto toDto(BoardList entity) {
        if (entity == null) {
            return null;
        }

        BoardListDto dto = new BoardListDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setPosition(entity.getPosition());
        dto.setBoard(
                new BoardDto().toDto(
                        entity.getBoard()
                ));
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

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

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public BoardDto getBoard() {
        return board;
    }

    public void setBoard(BoardDto board) {
        this.board = board;
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
        if (!(o instanceof BoardListDto that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}