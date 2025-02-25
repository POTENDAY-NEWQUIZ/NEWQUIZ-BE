package com.example.newquiz.service;

import com.example.newquiz.domain.CompletedNews;
import com.example.newquiz.domain.News;
import com.example.newquiz.domain.enums.NewsCategory;
import com.example.newquiz.dto.converter.NewsConverter;
import com.example.newquiz.dto.response.NewsResponse;
import com.example.newquiz.repository.CompletedNewsRepository;
import com.example.newquiz.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final CompletedNewsRepository completedNewsRepository;

    public NewsResponse.NewsListDto getNewsList(Long userId, String category) {
        log.info("getNewsList userId: {}, category: {}", userId, category);

        // 이미 퀴즈를 푼 뉴스는 제외
        List<CompletedNews> completedNews = completedNewsRepository.findByUserId(userId);

        // 뉴스 카테고리에 해당하는 뉴스 리스트를 가져옴
        List<News> newsList = newsRepository.findByCategory(NewsCategory.getNewsCategory(category));

        // 이미 퀴즈를 푼 뉴스는 제외
        newsList.removeIf(news -> completedNews.stream().anyMatch(completed -> completed.getNewsId().equals(news.getNewsId())));

        return NewsConverter.toNewsListDto(newsList, category);
    }
}
