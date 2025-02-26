package com.example.newquiz.dto.request;

import lombok.Data;

import java.util.List;

public class QuizRequest {

    @Data
    public static class QuizResultDto {
        private List<ResultDto> quizzes;
    }

    @Data
    public static class ResultDto {
        private Long quizId;
        private String type;
        private Boolean isCorrect;
        private Integer userAnswer;
    }
}
