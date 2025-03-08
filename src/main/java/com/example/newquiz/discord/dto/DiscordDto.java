package com.example.newquiz.discord.dto;

import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.request.LevelFeedbackRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

public class DiscordDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MessageDto {
        @JsonProperty("content")
        private String content;

        @JsonProperty("embeds")
        private List<EmbedDto> embeds;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class EmbedDto {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;
    }

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static class FeedbackDiscordDto {
        private String content;
        private Long newsId;
        private String newsTitle;
        private Long paragraphId;
        private String paragraphContent;
        private String userSummary;
        private String aiSummary;
        private String strength;
        private String improvement;
    }

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static class LevelFeedbackDiscordDto {
        private Long newsId;
        private String newsTitle;
        private String newsDate;
        private String source;
        private String level;
        private List<DiscordDto.FeedbackParagraphDto> feedbackParagraphs;
        private List<DiscordDto.FeedbackOptionQuizDto> feedbackOptionQuizDtos;
        private List<DiscordDto.FeedbackContentQuizDto> feedbackContentQuizzes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class FeedbackParagraphDto {
        private Long paragraphId;
        private String content;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class FeedbackOptionQuizDto {
        private Long quizId;
        private QuizType type;
        private Integer answer;
        private String word;
        private Long sourceParagraphId;
        private List<String> options;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class FeedbackContentQuizDto {
        private Long quizId;
        private QuizType type;
        private String question;
        private Boolean answer;
        private Long sourceParagraphId;
    }
}
