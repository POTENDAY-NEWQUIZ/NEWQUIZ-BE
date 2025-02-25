package com.example.newquiz.dto.request;

import com.example.newquiz.common.util.ResourceLoader;
import com.example.newquiz.domain.Paragraph;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class QuizCreateClovaRequest {
    private static final String SYSTEM_CONTENT = ResourceLoader.getResourceContent("quiz-generate-prompt.txt");

    private List<Map<String, String>> messages;
    private final double topP = 0.8;
    private final int topK = 0;
    private final int maxTokens = 700;
    private final double temperature = 0.5;
    private final double repeatPenalty = 5.0;
    private final boolean includeAiFilters = true;
    private final int seed = 0;

    public QuizCreateClovaRequest(List<Map<String, String>> messages) {
        this.messages = messages;
    }

    public static QuizCreateClovaRequest createQuizCreateClovaRequest(List<Paragraph> paragraphs) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 시스템 메시지 추가
        messages.add(Map.of("role", "system", "content", SYSTEM_CONTENT));

        String formattedContent = paragraphs.stream()
                .map(p -> String.format("[ParagraphID: %d]\n%s", p.getParagraphId(), p.getContent()))
                .collect(Collectors.joining("\n\n")); // 문단 간 개행 추가


        // 사용자 메시지 추가
        messages.add(Map.of("role", "user", "content", formattedContent));

        return new QuizCreateClovaRequest(messages);
    }
}
