package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.response.UserResponse;
import com.example.newquiz.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse.HomeInfoDto>> getHomeInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        UserResponse.HomeInfoDto homeInfo = homeService.getHomeInfo(customUserDetails.getUserId());
        return ApiResponse.success(SuccessStatus.GET_HOME_INFO_SUCCESS, homeInfo);
    }
}
