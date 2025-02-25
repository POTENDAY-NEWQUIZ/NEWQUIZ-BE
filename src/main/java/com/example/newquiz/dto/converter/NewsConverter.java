package com.example.newquiz.dto.converter;

import com.example.newquiz.domain.News;
import com.example.newquiz.dto.response.NewsResponse;

import java.util.List;
import java.util.stream.Collectors;

public class NewsConverter {
    public static NewsResponse.NewsListDto toNewsListDto(List<News> newsList, String category) {
        return NewsResponse.NewsListDto.builder()
                .selectedCategory(category)
                .newsCount(newsList.size())
                .news(newsList.stream()
                        .map(news -> NewsResponse.NewsDto.builder()
                                .newsId(news.getNewsId())
                                .title(news.getTitle())
                                .date(news.getDate())
                                .source(news.getSource())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
