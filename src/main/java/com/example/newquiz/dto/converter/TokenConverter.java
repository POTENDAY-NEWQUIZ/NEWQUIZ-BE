package com.example.newquiz.dto.converter;

import com.example.newquiz.dto.response.TokenResponse;

public class TokenConverter {
    public static TokenResponse.TokenDto toTokenDto(String newAccessToken, Long userId) {
        return TokenResponse.TokenDto.builder()
                .accessToken(newAccessToken)
                .userId(userId)
                .build();
    }
}
