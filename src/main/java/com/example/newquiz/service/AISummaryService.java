package com.example.newquiz.service;

import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.dto.request.SummaryRequest;
import com.example.newquiz.dto.response.SummaryResponse;

import java.util.List;

public interface AISummaryService {
    SummaryResponse.SummaryDto generateSummary(List<Paragraph> paragraphs, List<SummaryRequest.UserSummaryParagraph> summaryDto);
}
