package com.example.newquiz.dto.request;

import lombok.Data;

public class FeedbackRequest {
    @Data
    public static class FeedbackDto {
        private Long newsId;
        private Long paragraphId;
        private String content;
        private String userSummary;
        private String aiSummary;
        private String strength;
        private String improvement;
    }
}
