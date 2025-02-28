package com.example.newquiz.infra.clova;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ClovaUtil;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.infra.clova.dto.SummaryFeedbackClovaRequest;
import com.example.newquiz.dto.request.SummaryRequest;
import com.example.newquiz.dto.response.SummaryResponse;
import com.example.newquiz.service.AISummaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClovaAISummaryService implements AISummaryService {
    private final ClovaUtil clovaUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SummaryResponse.SummaryDto generateSummary(List<Paragraph> paragraphs, List<SummaryRequest.UserSummaryParagraph> summaryDto) {
        // Clova AI 요청 및 응답 파싱
        SummaryFeedbackClovaRequest request = SummaryFeedbackClovaRequest.createSummaryFeedbackClovaRequest(paragraphs, summaryDto);
        return parseSummaryResponse(request);
    }

    private SummaryResponse.SummaryDto parseSummaryResponse(SummaryFeedbackClovaRequest request) {
        try {
            String responseJson = clovaUtil.postWebClient(request);
            return objectMapper.readValue(clovaUtil.parseContentFromResponse(responseJson), SummaryResponse.SummaryDto.class);
        } catch (Exception e) {
            log.error("요약 생성 응답 파싱 실패: {}", e.getMessage());
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }
    }
}
