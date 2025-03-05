package com.example.newquiz.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizCreateResponse {

    @JsonProperty("questions")
    private List<Question> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Question {
        private String type;
        private String selectedWord;
        private String sentence;
        private List<String> options;
        private String answer;
        private String explanation;
        private String example;
        private int sourceParagraphId;
        private String question; // 내용일치 유형만 사용
    }
}
