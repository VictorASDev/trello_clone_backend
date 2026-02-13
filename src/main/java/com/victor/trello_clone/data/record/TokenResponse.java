package com.victor.trello_clone.data.record;

public record TokenResponse(
        String accessToken,
        long accessExpiresIn,
        String refreshToken,
        long refreshExpiresAt
) {}