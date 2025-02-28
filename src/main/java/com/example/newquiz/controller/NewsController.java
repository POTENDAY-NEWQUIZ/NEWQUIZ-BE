package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.request.LevelFeedbackRequest;
import com.example.newquiz.dto.request.SummaryRequest;
import com.example.newquiz.dto.response.NewsResponse;
import com.example.newquiz.dto.response.SummaryResponse;
import com.example.newquiz.service.NewsService;
import com.example.newquiz.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final SummaryService summaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<NewsResponse.NewsListDto>> getNewsList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("category") String category
    ) {
        NewsResponse.NewsListDto response = newsService.getNewsList(userDetails.getUserId(), category);
        return ApiResponse.success(SuccessStatus.NEWS_LIST_SUCCESS, response);
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<ApiResponse<NewsResponse.NewsDetailDto>> getNewsDetail(
            @PathVariable Long newsId
    ) {
        NewsResponse.NewsDetailDto response = newsService.getNewsDetail(newsId);
        return ApiResponse.success(SuccessStatus.NEWS_DETAIL_SUCCESS, response);
    }

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse.SummaryDto>> saveSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SummaryRequest.SummaryDto summaryDto
    ) {
        SummaryResponse.SummaryDto response = summaryService.saveSummary(userDetails.getUserId(), summaryDto);
        return ApiResponse.success(SuccessStatus.SAVE_SUMMARY_SUCCESS, response);
    }

    @PostMapping("/level")
    public ResponseEntity<ApiResponse<String>> sendLevelFeedback(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody LevelFeedbackRequest.LevelFeedbackDto levelFeedbackDto
    ) {
        newsService.sendLevelFeedback(userDetails.getUserId(), levelFeedbackDto);
        return ApiResponse.success(SuccessStatus.SEND_LEVEL_FEEDBACK_SUCCESS);
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<ApiResponse<String>> deleteNews(
            @PathVariable Long newsId
    ) {
        newsService.deleteNews(newsId);
        return ApiResponse.success(SuccessStatus.DELETE_NEWS_SUCCESS);
    }

}
