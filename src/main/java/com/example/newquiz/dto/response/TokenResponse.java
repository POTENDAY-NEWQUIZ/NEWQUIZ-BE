package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class TokenResponse {

    @Data
    @Builder
    @AllArgsConstructor
    public static class TokenDto {
        private Long userId;
        private String accessToken;
    }
}
