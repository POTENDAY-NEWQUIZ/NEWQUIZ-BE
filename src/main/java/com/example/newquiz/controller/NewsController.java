package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.response.NewsResponse;
import com.example.newquiz.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

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


}
