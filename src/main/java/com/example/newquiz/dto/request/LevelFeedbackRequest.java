package com.example.newquiz.dto.request;

import lombok.Data;

public class LevelFeedbackRequest {
    @Data
    public static class LevelFeedbackDto {
        private Long newsId;
        private String level;
    }
}
