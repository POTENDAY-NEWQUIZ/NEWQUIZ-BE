package com.example.newquiz.service;

import com.example.newquiz.common.util.JwtUtil;
import com.example.newquiz.dto.converter.TokenConverter;
import com.example.newquiz.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtil jwtUtil;

    public TokenResponse.TokenDto reissueToken(String refreshToken) {
        jwtUtil.validateToken(refreshToken);
        String userId = jwtUtil.getUserIdFromToken(refreshToken);

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        return TokenConverter.toTokenDto(newAccessToken, Long.parseLong(userId));
    }
}
