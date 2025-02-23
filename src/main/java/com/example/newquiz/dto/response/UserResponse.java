package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class UserResponse {

    @Data
    @Builder
    @AllArgsConstructor
    public static class UserDto {
        private Long userId;
        private String nickname;
        private String accessToken;
        private String refreshToken;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class NickNameCheckDto {
        private Boolean isDuplicate;
    }

}
