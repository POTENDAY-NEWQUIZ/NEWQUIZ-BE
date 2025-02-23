package com.example.newquiz.common.util;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.register-token.expiration-time}")
    private long REGISTER_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    // SecretKey 생성
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 액세스 토큰 생성
    public String generateAccessToken(String userId) {
        log.info("액세스 토큰이 발행되었습니다.");

        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(this.getSigningKey())
                .compact();
    }

    // 리프레쉬 토큰 생성
    public String generateRefreshToken(String userId) {
        log.info("리프레쉬 토큰이 발행되었습니다.");

        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(this.getSigningKey())
                .compact();
    }

    // 레지스터 토큰 생성
    public String generateRegisterToken(String providerId) {
        log.info("레지스터 토큰이 발행되었습니다.");

        return Jwts.builder()
                .claim("providerId", providerId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REGISTER_TOKEN_EXPIRATION_TIME))
                .signWith(this.getSigningKey())
                .compact();
    }

    // 헤더에서 토큰 추출
    public String extractToken(String header) {
        return header.substring(7);
    }

    // 토큰에서 UserId 추출
    public String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(this.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
    }

    // 토큰에서 ProviderId 추출
    public String getProviderIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(this.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("providerId", String.class);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(this.getSigningKey()).build().parseSignedClaims(token).getPayload();
            return true;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }
    }


}
