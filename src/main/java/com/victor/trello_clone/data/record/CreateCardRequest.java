package com.victor.trello_clone.data.record;

import jakarta.validation.constraints.NotBlank;

public record CreateCardRequest(
        @NotBlank String title,
        String description) {
}
