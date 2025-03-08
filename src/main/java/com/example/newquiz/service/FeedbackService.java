package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.discord.DiscordAlarmSender;
import com.example.newquiz.discord.converter.FeedbackConverter;
import com.example.newquiz.discord.dto.DiscordDto;
import com.example.newquiz.domain.News;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.dto.request.FeedbackRequest;
import com.example.newquiz.repository.NewsRepository;
import com.example.newquiz.repository.ParagraphRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final NewsRepository newsRepository;
    private final ParagraphRepository paragraphRepository;
    private final DiscordAlarmSender discordAlarmSender;

    public void sendAIFeedback(Long userId, FeedbackRequest.FeedbackDto feedbackDto) {
        // 해당 뉴스 가져오기
        News news = newsRepository.findById(feedbackDto.getNewsId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        // 해당 뉴스의 문단 가져오기
        Paragraph paragraph = paragraphRepository.findById(feedbackDto.getParagraphId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        DiscordDto.FeedbackDiscordDto discordDto = FeedbackConverter.toFeedbackDiscordDto(news, paragraph, feedbackDto);

        // 메시지 생성 및 디스코드 전송
        discordAlarmSender.sendAIFeedbackDiscordAlarm(discordDto);
    }

}
