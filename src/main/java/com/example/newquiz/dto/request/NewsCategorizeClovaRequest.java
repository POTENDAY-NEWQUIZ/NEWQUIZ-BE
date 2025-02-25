package com.example.newquiz.dto.request;

import com.example.newquiz.common.util.ResourceLoader;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class NewsCategorizeClovaRequest {
    private static final String SYSTEM_CONTENT = ResourceLoader.getResourceContent("news-categorize-prompt.txt");

    private List<Map<String, String>> messages;
    private final double topP = 0.8;
    private final int topK = 0;
    private final int maxTokens = 700;
    private final double temperature = 0.5;
    private final double repeatPenalty = 5.0;
    private final boolean includeAiFilters = true;
    private final int seed = 0;

    public NewsCategorizeClovaRequest(List<Map<String, String>> messages) {
        this.messages = messages;
    }


    public static NewsCategorizeClovaRequest createNewsCategorizeRequest(List<String> newsContents) {
        // newsContents를 하나의 String으로 합치기 (문단 간 개행 추가)
        String combinedNewsContent = String.join("\n", newsContents);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_CONTENT));
        messages.add(Map.of("role", "user", "content", combinedNewsContent));

        return new NewsCategorizeClovaRequest(messages);
    }

}
