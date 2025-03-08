package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class NoteResponse {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoteListDto {
        List<NoteDto> notes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoteDetailDto {
        private Long quizResultId;
        private NewsResponse.NewsDetailDto news;
        private QuizResponse.SynonymQuizDto synonymQuiz;
        private QuizResponse.MeaningQuizDto meaningQuiz;
        private QuizResponse.ContentQuizDto contentQuiz;
    }
}
