package com.victor.trello_clone.data.record;

public record MoveCardRequest(Long targetListId, Long position) {
}
