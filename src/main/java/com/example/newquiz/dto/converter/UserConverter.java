package com.example.newquiz.dto.converter;

import com.example.newquiz.dto.response.UserResponse;

public class UserConverter {
    public static UserResponse.UserDto toUserDto(Long userId, String nickname, String accessToken) {
        return UserResponse.UserDto.builder()
                .userId(userId)
                .nickname(nickname)
                .accessToken(accessToken)
                .build();
    }

    public static UserResponse.NickNameCheckDto toNickNameCheckDto(Boolean isDuplicate) {
        return UserResponse.NickNameCheckDto.builder()
                .isDuplicate(isDuplicate)
                .build();
    }
}
