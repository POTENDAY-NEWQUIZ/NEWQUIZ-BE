package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.response.NewsResponse;
import com.example.newquiz.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
