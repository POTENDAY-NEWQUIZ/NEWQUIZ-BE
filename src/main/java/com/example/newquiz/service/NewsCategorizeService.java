package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ClovaUtil;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.domain.enums.NewsCategory;
import com.example.newquiz.dto.request.NewsCategorizeClovaRequest;
import com.example.newquiz.dto.response.NewsCategorizeResponse;
import com.example.newquiz.repository.NewsRepository;
import com.example.newquiz.repository.ParagraphRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsCategorizeService {

    private final ClovaUtil clovaUtil;
    private final NewsRepository newsRepository;
    private final ParagraphRepository paragraphRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void categorizeNews(Long newsId) {
        try {
            // 뉴스 문단 가져오기
            List<String> newsContents = paragraphRepository.findByNewsIdOrderByContentOrderAsc(newsId)
                    .stream()
                    .map(Paragraph::getContent)
                    .collect(Collectors.toList());

            // Clova API 요청
            NewsCategorizeClovaRequest clovaRequest = NewsCategorizeClovaRequest.createNewsCategorizeRequest(newsContents);
            String response = clovaUtil.postWebClient(clovaRequest);

            // AI 응답 파싱
            NewsCategorizeResponse newsCategorizeResponse = parseNewsCategorizeResponse(clovaUtil.parseContentFromResponse(response));

            // 카테고리 매핑 후 저장
            NewsCategory category = switch (newsCategorizeResponse.getCategory()) {
                case "정치" -> NewsCategory.POLITICS;
                case "경제" -> NewsCategory.ECONOMY;
                case "사회" -> NewsCategory.SOCIETY;
                case "글로벌" -> NewsCategory.GLOBAL;
                default -> throw new GeneralException(ErrorStatus.NEWS_CATEGORY_INVALID_AI_RESPONSE);
            };

            updateNewsCategory(newsId, category);

        } catch (Exception e) {
            log.error("🚨 뉴스 ID {} 카테고리 분류 실패: {}", newsId, e.getMessage(), e);
            newsRepository.deleteById(newsId);
            paragraphRepository.deleteAllByNewsId(newsId);
        }
    }

    private NewsCategorizeResponse parseNewsCategorizeResponse(String aiResponse) {
        try {
            return objectMapper.readValue(aiResponse, NewsCategorizeResponse.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.NEWS_CATEGORY_INVALID_AI_RESPONSE);
        }
    }

    private void updateNewsCategory(Long newsId, NewsCategory category) {
        newsRepository.findById(newsId)
                .ifPresent(news -> {
                    news.setCategory(category);
                    newsRepository.save(news);
                });
    }
}
