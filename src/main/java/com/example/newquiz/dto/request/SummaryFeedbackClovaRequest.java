package com.example.newquiz.dto.request;

import com.example.newquiz.common.util.ResourceLoader;
import com.example.newquiz.domain.Paragraph;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class SummaryFeedbackClovaRequest {
    private static final String SYSTEM_CONTENT = ResourceLoader.getResourceContent("summary-feedback-prompt.txt");

    private List<Map<String, String>> messages;
    private final double topP = 0.8;
    private final int topK = 0;
    private final int maxTokens = 1000;
    private final double temperature = 0.5;
    private final double repeatPenalty = 5.0;
    private final boolean includeAiFilters = true;
    private final int seed = 0;

    public SummaryFeedbackClovaRequest(List<Map<String, String>> messages) {
        this.messages = messages;
    }

    public static SummaryFeedbackClovaRequest createSummaryFeedbackClovaRequest(List<Paragraph> paragraphs, List<SummaryRequest.UserSummaryParagraph> summaryDto) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 시스템 메시지 추가
        messages.add(Map.of("role", "system", "content", SYSTEM_CONTENT));

        String formattedContent = paragraphs.stream()
                .map(p -> String.format("[ParagraphId: %d]\nanswerSummary: %s\nuserSummary: %s",
                        p.getParagraphId(),
                        p.getSummary(),
                        summaryDto.stream()
                                .filter(s -> s.getParagraphId().equals(p.getParagraphId()))
                                .map(SummaryRequest.UserSummaryParagraph::getUserSummary)
                                .findFirst()
                                .orElse("")
                ))
                .collect(Collectors.joining("\n\n")); // 문단 간 개행 추가


        // 사용자 메시지 추가
        messages.add(Map.of("role", "user", "content", formattedContent));

        return new SummaryFeedbackClovaRequest(messages);
    }
}
