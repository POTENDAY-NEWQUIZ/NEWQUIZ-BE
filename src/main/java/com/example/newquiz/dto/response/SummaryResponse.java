package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class SummaryResponse {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SummaryDto {
        private int totalScore;
        private String generalFeedback;
        private List<AISummaryParagraph> paragraphs;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AISummaryParagraph {
        private Long paragraphId;
        private String aiSummary;
        private String strengths;
        private String improvements;
    }

}
