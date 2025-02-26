package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class QuizResponse {

    @Data
    @Builder
    @AllArgsConstructor
    public static class QuizListDto{
        private int totalQuizCount;
        private int synonymQuizCount;
        private int meaningQuizCount;
        private int contentQuizCount;
        private List<Long> quizIdList;
        private List<SynonymQuizDto> synonymQuiz;
        private List<MeaningQuizDto> meaningQuiz;
        private List<ContentQuizDto> contentQuiz;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class SynonymQuizDto {
        private String type;
        private Long quizId;
        private Long paragraphId;
        private String sourceSentence;
        private String word;
        private String option1;
        private String option2;
        private String option3;
        private String option4;
        private int answer;
        private String explanation;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class MeaningQuizDto {
        private String type;
        private Long quizId;
        private Long paragraphId;
        private String sourceSentence;
        private String word;
        private String option1;
        private String option2;
        private String option3;
        private String option4;
        private int answer;
        private String explanation;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ContentQuizDto {
        private String type;
        private Long quizId;
        private Long paragraphId;
        private String question;
        private Boolean answer;
        private String explanation;

    }
}