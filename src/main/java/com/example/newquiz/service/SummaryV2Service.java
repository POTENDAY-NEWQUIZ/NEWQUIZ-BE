package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.common.util.ClovaUtil;
import com.example.newquiz.domain.News;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.dto.request.SummaryCreateClovaRequest;
import com.example.newquiz.dto.request.SummaryFeedbackClovaRequest;
import com.example.newquiz.dto.response.SummaryCreateResponse;
import com.example.newquiz.dto.response.SummaryResponse;
import com.example.newquiz.repository.NewsRepository;
import com.example.newquiz.repository.ParagraphRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryV2Service {

    private final ClovaUtil clovaUtil;
    private final ParagraphRepository paragraphRepository;
    private final NewsRepository newsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void saveSummary(News news) {
        List<Paragraph> paragraphs = paragraphRepository.findByNewsId(news.getNewsId());

        SummaryCreateResponse response = parseSummaryResponse(
                SummaryCreateClovaRequest.createSummaryClovaRequest(paragraphs));

        if (response == null) {
            log.info("요약 응답이 null입니다. 다시 요청합니다.");
            response = parseSummaryResponse(SummaryCreateClovaRequest.createSummaryClovaRequest(paragraphs));
        }

        if (response == null ||  response.getTotalSummary() == null) {
            log.error("재요청도 실패");
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }

        // 각 문단에 각 response안의 문단 id에 해당하는 요약 결과를 저장
        for (Paragraph paragraph : paragraphs) {
            String summary = response.getSummaries().stream()
                    .filter(r -> r.getParagraphId().equals(paragraph.getParagraphId()))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_AI_RESPONSE))
                    .getAiSummary();

            if (summary == null) {
                log.error("요약 결과가 null입니다.");
                throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
            }
            paragraph.setSummary(summary);
        }

        if (response.getTotalSummary() == null) {
            log.error("전체 요약 결과가 null입니다.");
            throw new GeneralException(ErrorStatus.INVALID_AI_RESPONSE);
        }
        news.setTotalSummary(response.getTotalSummary());
        newsRepository.save(news);
    }

    /**
     * Clova AI API를 호출하여 요약 결과를 가져옴.
     */
    private SummaryCreateResponse parseSummaryResponse(SummaryCreateClovaRequest request) {
        try {
            String responseJson = clovaUtil.postWebClient(request);
            log.info("요약 응답: {}", responseJson);
            return objectMapper.readValue(clovaUtil.parseContentFromResponse(responseJson), SummaryCreateResponse.class);
        } catch (Exception e) {
            log.error("요약 응답 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

}
