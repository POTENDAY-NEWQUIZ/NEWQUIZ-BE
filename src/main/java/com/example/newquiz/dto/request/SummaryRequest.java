package com.example.newquiz.dto.request;

import lombok.Data;

import java.util.List;

public class SummaryRequest {
    @Data
    public static class SummaryDto {
        private Long newsId;
        private List<UserSummaryParagraph> paragraphs;
    }

    @Data
    public static class UserSummaryParagraph {
        private Long paragraphId;
        private String userSummary;
    }
}
