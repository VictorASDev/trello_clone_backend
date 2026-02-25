package com.victor.trello_clone.data.record;

import com.victor.trello_clone.data.dto.UserDto;

public record LoginResponse(String accessToken, long accessExpiresIn, UserDto user) {
}
