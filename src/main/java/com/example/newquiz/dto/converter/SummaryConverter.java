package com.example.newquiz.dto.converter;

import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.dto.response.SummaryResponse;

import java.util.List;

public class SummaryConverter {
    public static SummaryResponse.SummaryFeedback convertToSummaryFeedback(SummaryResponse.SummaryDto summaryDto, List<Paragraph> paragraphs) {
        return SummaryResponse.SummaryFeedback.builder()
                .totalScore(summaryDto.getTotalScore())
                .generalFeedback(summaryDto.getGeneralFeedback())
                .paragraphs(summaryDto.getParagraphs().stream()
                        .map(aiSummaryParagraph -> convertToSummaryParagraph(aiSummaryParagraph, paragraphs))
                        .toList())
                .build();
    }

    public static SummaryResponse.SummaryParagraph convertToSummaryParagraph(SummaryResponse.AISummaryParagraph aiSummaryParagraph, List<Paragraph> paragraphs) {
        return SummaryResponse.SummaryParagraph.builder()
                .paragraphId(aiSummaryParagraph.getParagraphId())
                .strengths(aiSummaryParagraph.getStrengths())
                .improvements(aiSummaryParagraph.getImprovements())
                .aiSummary(paragraphs.stream()
                        .filter(paragraph -> paragraph.getParagraphId().equals(aiSummaryParagraph.getParagraphId()))
                        .findFirst()
                        .map(Paragraph::getSummary)
                        .orElse(null))
                .build();
    }
}
