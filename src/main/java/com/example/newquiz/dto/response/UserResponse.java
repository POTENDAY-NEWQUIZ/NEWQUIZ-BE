package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

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

    @Data
    @Builder
    @AllArgsConstructor
    public static class HomeInfoDto {
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer learningDays;
        private Integer maxLearningDays;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class MyPageDto {
        private String nickname;
        private String profileImageUrl;
        private Integer userQuizCount;
        private Integer maxLearningDays;
        private Integer learningDays;
        private double avgScore;
        private double maxAvgScore;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ProfileImageDto {
        private String profileImageUrl;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class UserStudyInfoDto {
        private String nickName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer learningDays;
        private Integer maxLearningDays;
        private Integer totalCount;
        private List<GraphDto> graph;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class GraphDto {
        private LocalDate date;
        private String dayOfWeek;
        private Integer count;
    }
}
