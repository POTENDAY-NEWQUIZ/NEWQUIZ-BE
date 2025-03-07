package com.example.newquiz.dto.converter;

import com.example.newquiz.domain.News;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.domain.enums.NewsCategory;
import com.example.newquiz.dto.response.NewsResponse;

import java.util.List;
import java.util.stream.Collectors;

public class NewsConverter {
    public static NewsResponse.NewsListDto toNewsListDto(List<News> newsList, String category, String level) {
        return NewsResponse.NewsListDto.builder()
                .selectedCategory(category)
                .selectedLevel(level)
                .newsCount(newsList.size())
                .news(newsList.stream()
                        .map(news -> NewsResponse.NewsDto.builder()
                                .newsId(news.getNewsId())
                                .level(news.getLevel())
                                .title(news.getTitle())
                                .date(news.getDate())
                                .source(news.getSource())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static NewsResponse.NewsDetailDto toNewsDetailDto(News news, List<Paragraph> paragraphs) {
        return NewsResponse.NewsDetailDto.builder()
                .newsId(news.getNewsId())
                .totalSummary(news.getTotalSummary())
                .title(news.getTitle())
                .date(news.getDate())
                .source(news.getSource())
                .category(news.getCategory().getValue())
                .paragraphs(paragraphs.stream()
                        .map(paragraph -> NewsResponse.ParagraphDto.builder()
                                .paragraphId(paragraph.getParagraphId())
                                .order(paragraph.getContentOrder())
                                .content(paragraph.getContent())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
