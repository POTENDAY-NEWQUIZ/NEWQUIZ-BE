package com.example.newquiz.auth.handler;

import com.example.newquiz.auth.dto.KakaoUserInfo;
import com.example.newquiz.auth.dto.OAuth2UserInfo;
import com.example.newquiz.common.util.JwtUtil;
import com.example.newquiz.domain.RefreshToken;
import com.example.newquiz.domain.User;
import com.example.newquiz.repository.RefreshTokenRepository;
import com.example.newquiz.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.redirect.access}")
    private String ACCESS_TOKEN_REDIRECT_URI;

    @Value("${jwt.redirect.register}")
    private String REGISTER_TOKEN_REDIRECT_URI;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2UserInfo oAuth2UserInfo = new KakaoUserInfo(token.getPrincipal().getAttributes());

        String providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();
        log.info("providerId: {}", providerId);
        log.info("name: {}", name);

        Optional<User> optionalUser = userRepository.findByProviderId(providerId);

        if (optionalUser.isPresent()) {
            handleExistingUser(request, response, optionalUser.get());
        } else {
            handleNewUser(request, response, providerId);
        }
    }

    private void handleExistingUser(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        log.info("기존 유저입니다.");
        // 기존 RefreshToken 삭제
        deleteRefreshTokenByUserId(user.getUserId());

        String accessToken = jwtUtil.generateAccessToken(user.getUserId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getUserId())
                .refreshToken(refreshToken)
                .build());

        String redirectURI = String.format(ACCESS_TOKEN_REDIRECT_URI, accessToken, refreshToken);
        getRedirectStrategy().sendRedirect(request, response, redirectURI);
    }

    private void deleteRefreshTokenByUserId(Long userId) {
        Iterable<RefreshToken> tokens = refreshTokenRepository.findAll();
        for (RefreshToken token : tokens) {
            if (token.getUserId().equals(userId)) {
                refreshTokenRepository.delete(token);
            }
        }
    }

    private void handleNewUser(HttpServletRequest request, HttpServletResponse response, String providerId) throws IOException {
        log.info("신규 유저입니다.");
        String registerToken = jwtUtil.generateRegisterToken(providerId);
        String redirectURI = String.format(REGISTER_TOKEN_REDIRECT_URI, registerToken);
        getRedirectStrategy().sendRedirect(request, response, redirectURI);
    }
}
