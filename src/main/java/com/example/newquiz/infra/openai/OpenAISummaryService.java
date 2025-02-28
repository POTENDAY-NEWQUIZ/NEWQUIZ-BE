package com.example.newquiz.infra.openai;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ResourceLoader;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.dto.request.SummaryRequest;
import com.example.newquiz.dto.response.SummaryResponse;
import com.example.newquiz.infra.clova.ClovaAISummaryService;
import com.example.newquiz.service.AISummaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class OpenAISummaryService implements AISummaryService {
    private final OpenAiChatModel chatModel;
    private static final String SYSTEM_CONTENT = ResourceLoader.getResourceContent("summary-feedback-prompt.txt");
    private final ClovaAISummaryService clovaAISummaryService;


    @Override
    public SummaryResponse.SummaryDto generateSummary(List<Paragraph> paragraphs, List<SummaryRequest.UserSummaryParagraph> summaryDto) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 시스템 메시지 추가
        addSystemMessage(messages, SYSTEM_CONTENT);

        // 사용자 입력 추가
        addUserSummary(messages, paragraphs, summaryDto);

        String response;
        try {
            response = chatModel.call(String.valueOf(messages));
        } catch (HttpServerErrorException e) {
            return clovaAISummaryService.generateSummary(paragraphs, summaryDto);
        }
        return parseSummaryResponse(response);
    }

    private void addSystemMessage(List<Map<String, String>> messages, String systemContent) {
        messages.add(Map.of(
                "role", "system",
                "content", systemContent
        ));
    }

    private void addUserSummary(List<Map<String, String>> messages, List<Paragraph> paragraphs, List<SummaryRequest.UserSummaryParagraph> summaryDto) {
        String formattedContent = paragraphs.stream()
                .map(p -> String.format("[ParagraphId: %d]\n원문: %s\n사용자 요약: %s",
                        p.getParagraphId(),
                        p.getContent(),
                        summaryDto.stream()
                                .filter(s -> s.getParagraphId().equals(p.getParagraphId()))
                                .map(SummaryRequest.UserSummaryParagraph::getUserSummary)
                                .findFirst()
                                .orElse("")
                ))
                .collect(Collectors.joining("\n\n")); // 문단 간 개행 추가

        // 사용자 메시지 추가
        messages.add(Map.of("role", "user", "content", formattedContent));
    }
    private SummaryResponse.SummaryDto parseSummaryResponse(String aiResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(aiResponse, SummaryResponse.SummaryDto.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }
    }
}
