package com.example.newquiz.discord.converter;

import com.example.newquiz.discord.dto.DiscordDto;
import com.example.newquiz.domain.*;
import com.example.newquiz.dto.request.LevelFeedbackRequest;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackConverter {
    public static DiscordDto.LevelFeedbackDiscordDto toLevelFeedbackDiscordDto(News news, LevelFeedbackRequest.LevelFeedbackDto levelFeedback, List<Paragraph> paragraphs, List<DiscordDto.FeedbackOptionQuizDto> feedbackOptionQuizDtos, List<DiscordDto.FeedbackContentQuizDto> feedbackContentQuizzes) {
        return DiscordDto.LevelFeedbackDiscordDto.builder()
                .newsId(news.getNewsId())
                .newsTitle(news.getTitle())
                .newsDate(news.getDate().toString())
                .source(news.getSource())
                .level(levelFeedback.getLevel())
                .feedbackParagraphs(paragraphs.stream()
                        .map(FeedbackConverter::toFeedbackParagraphDto)
                        .collect(Collectors.toList()))
                .feedbackOptionQuizDtos(feedbackOptionQuizDtos)
                .feedbackContentQuizzes(feedbackContentQuizzes)
                .build();

    }

    public static DiscordDto.FeedbackParagraphDto toFeedbackParagraphDto(Paragraph paragraph) {
        return DiscordDto.FeedbackParagraphDto.builder()
                .paragraphId(paragraph.getParagraphId())
                .content(paragraph.getContent())
                .build();
    }

    public static DiscordDto.FeedbackOptionQuizDto toFeedbackSynonymQuizDto(Quiz quiz, SynonymQuiz synonymQuiz) {
        return DiscordDto.FeedbackOptionQuizDto.builder()
                .quizId(quiz.getQuizId())
                .type(quiz.getType())
                .answer(synonymQuiz.getAnswer())
                .word(synonymQuiz.getWord())
                .sourceParagraphId(quiz.getParagraphId())
                .options(synonymQuiz.getOptions())
                .build();
    }

    public static DiscordDto.FeedbackOptionQuizDto toFeedbackMeaningQuizDto(Quiz quiz, MeaningQuiz meaningQuiz) {
        return DiscordDto.FeedbackOptionQuizDto.builder()
                .quizId(quiz.getQuizId())
                .type(quiz.getType())
                .word(meaningQuiz.getWord())
                .answer(meaningQuiz.getAnswer())
                .sourceParagraphId(quiz.getParagraphId())
                .options(meaningQuiz.getOptions())
                .build();
    }

    public static DiscordDto.FeedbackContentQuizDto toFeedbackContentQuizDto(Quiz quiz, ContentQuiz contentQuiz) {
        return DiscordDto.FeedbackContentQuizDto.builder()
                .quizId(quiz.getQuizId())
                .type(quiz.getType())
                .question(contentQuiz.getQuestion())
                .answer(contentQuiz.getAnswer())
                .sourceParagraphId(quiz.getParagraphId())
                .build();
    }
}
