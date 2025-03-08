package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.response.RankingResponse;
import com.example.newquiz.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<ApiResponse<RankingResponse.RankingDto>> getRanking(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        RankingResponse.RankingDto ranking = rankingService.getRanking(customUserDetails.getUserId());
        return ApiResponse.success(SuccessStatus.RANKING_GET_SUCCESS, ranking);
    }
}
