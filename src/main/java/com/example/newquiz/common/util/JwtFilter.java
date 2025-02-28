package com.example.newquiz.common.util;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.auth.service.CustomUserDetailsService;
import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // JWT 토큰을 검증하고, 토큰이 유효하면 SecurityContext에 인증 정보를 저장
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtUtil.extractToken(request.getHeader("Authorization"));
            jwtUtil.validateToken(token);
            String userId = jwtUtil.getUserIdFromToken(token);

            setAuthentication(userId);

        } catch (Exception e) {
            log.error("JWT 토큰 검증 실패 " + e.getMessage());
            handleException(response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String userId) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUserId(Long.parseLong(userId));
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }


    // 특정 경로는 필터링하지 않도록 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 특정 경로는 필터링하지 않도록 설정
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/authorization/kakao") || path.startsWith("/api/users/register") || path.startsWith("/api/tokens/issue") || path.startsWith("/api/users/nickname/check")|| path.startsWith("/actuator/health");
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = String.format("{\"isSuccess\": \"false\", \"code\": \"0401\", \"message\": \"%s\"}", e.getMessage());
        response.getWriter().write(jsonResponse);
    }

}
