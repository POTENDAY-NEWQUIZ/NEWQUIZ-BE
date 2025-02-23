package com.example.newquiz.controller;

import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.response.TokenResponse;
import com.example.newquiz.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tokens")
public class TokenController {
    private final TokenService tokenService;

    @GetMapping("/issue")
    public ResponseEntity<ApiResponse<TokenResponse.TokenDto>> reissueToken(
            @RequestHeader("refreshToken") String refreshToken
    ) {
        TokenResponse.TokenDto tokenDto = tokenService.reissueToken(refreshToken);
        return ApiResponse.success(SuccessStatus.TOKEN_REISSUE_SUCCESS, tokenDto);
    }

}
