package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class NewsResponse {

    @Data
    @Builder
    @AllArgsConstructor
    public static class NewsListDto {
        private String selectedCategory;
        private int newsCount;
        private List<NewsDto> news;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class NewsDto {
        private Long newsId;
        private String title;
        private LocalDate date;
        private String source;
    }
}
