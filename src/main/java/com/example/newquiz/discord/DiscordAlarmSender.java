package com.example.newquiz.discord;

import com.example.newquiz.discord.dto.DiscordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DiscordAlarmSender {
    private final Environment environment;
    private final DiscordUtil discordUtil;
    private final WebClient webClient = WebClient.create();

    @Value("${logging.discord.feedback-web-hook-url}")
    private String feedbackWebHookUrl;

    public void sendFeedbackDiscordAlarm(DiscordDto.LevelFeedbackDiscordDto feedbackDto) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            webClient.post()
                    .uri(feedbackWebHookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(discordUtil.createLevelFeedbackMessage(feedbackDto))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }

    public void sendAIFeedbackDiscordAlarm(DiscordDto.FeedbackDiscordDto feedbackDto) {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            webClient.post()
                    .uri(feedbackWebHookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(discordUtil.createAIFeedbackMessage(feedbackDto))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }
}
