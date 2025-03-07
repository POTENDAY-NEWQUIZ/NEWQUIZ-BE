package com.example.newquiz.dto.converter;

import com.example.newquiz.domain.User;
import com.example.newquiz.dto.response.UserResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {
    public static UserResponse.UserDto toUserDto(Long userId, String nickname, String accessToken, String refreshToken) {
        return UserResponse.UserDto.builder()
                .userId(userId)
                .nickname(nickname)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponse.NickNameCheckDto toNickNameCheckDto(Boolean isDuplicate) {
        return UserResponse.NickNameCheckDto.builder()
                .isDuplicate(isDuplicate)
                .build();
    }

    public static UserResponse.MyPageDto toMyPageDto(User user, int learningDays, int userQuizCount) {
        return UserResponse.MyPageDto.builder()
                .nickname(user.getNickName())
                .profileImageUrl(user.getProfileImageUrl())
                .maxLearningDays(user.getMaxLearningDays())
                .userQuizCount(userQuizCount)
                .learningDays(learningDays)
                .avgScore(user.getAvgScore())
                .maxAvgScore(user.getMaxAvgScore())
                .build();
    }

    public static UserResponse.UserStudyInfoDto toUserStudyInfoDto(User user, LocalDate startDate, LocalDate endDate, int learningDays, int totalCount, List<UserResponse.GraphDto> graph) {
        return UserResponse.UserStudyInfoDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .learningDays(learningDays)
                .maxLearningDays(user.getMaxLearningDays())
                .totalCount(totalCount)
                .graph(graph)
                .build();
    }

}
