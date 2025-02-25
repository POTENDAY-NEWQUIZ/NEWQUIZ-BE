package com.example.newquiz.common.util;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ClovaUtil {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.create();

    @Value("${ncp.chat.host}")
    private String chatHost;

    @Value("${ncp.chat.api-key}")
    private String API_KEY;

    public String postWebClient(Object clovaRequest) {
        return webClient.post()
                .uri(chatHost)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(clovaRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("클라이언트 오류 발생: 상태 코드 - {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .map(errorBody -> new GeneralException(ErrorStatus.AI_CLIENT_ERROR));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.error("서버 오류 발생: 상태 코드 - {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .map(errorBody -> new GeneralException(ErrorStatus.AI_SERVER_ERROR));
                })
                .bodyToMono(String.class)
                .block();
    }

    public String parseContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode messageContent = root.path("result").path("message").path("content");
            return messageContent.asText();
        } catch (Exception e) {
            log.error("응답 파싱 실패", e);
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }
    }
}

