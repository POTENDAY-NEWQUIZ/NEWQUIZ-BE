package com.example.newquiz.discord;

import com.example.newquiz.discord.dto.DiscordDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiscordUtil {

    /**
     * AI Feedback 정보를 Discord 메시지로 변환
     */
    public DiscordDto.MessageDto createAIFeedbackMessage(DiscordDto.FeedbackDiscordDto aiFeedbackDto) {
        return DiscordDto.MessageDto.builder()
                .content("# 📚 AI 불만족 피드백 🚨")
                .embeds(List.of(
                        DiscordDto.EmbedDto.builder()
                                        .title("🚨 피드백 내용")
                                        .description(aiFeedbackDto.getContent())
                                        .build(),
                        DiscordDto.EmbedDto.builder()
                                .title("📰 뉴스 정보")
                                .description(
                                        "**뉴스 ID:** " + aiFeedbackDto.getNewsId() + "\n" +
                                        "**뉴스 제목:** " + aiFeedbackDto.getNewsTitle() + "\n"
                                )
                                .build(),
                        DiscordDto.EmbedDto.builder()
                                .title("📖 문단 정보")
                                .description(
                                        "**문단 ID:** " + aiFeedbackDto.getParagraphId() + "\n" +
                                        "**문단 내용:** " + aiFeedbackDto.getParagraphContent() + "\n"
                                )
                                .build(),
                        DiscordDto.EmbedDto.builder()
                                .title("🙋 사용자 입력")
                                .description(
                                        "**사용자 요약:** " + aiFeedbackDto.getUserSummary()
                                )
                                .build(),
                        DiscordDto.EmbedDto.builder()
                                .title("🤖 AI 피드백")
                                .description(
                                        "**AI 요약:** " + aiFeedbackDto.getAiSummary() + "\n" +
                                                "**잘한점:** " + aiFeedbackDto.getStrength() + "\n" +
                                                "**보완할점:** " + aiFeedbackDto.getImprovement()
                                )
                                .build()
                )).build();
    }

    /**
     * Level Feedback 정보를 Discord 메시지로 변환
     */
    public DiscordDto.MessageDto createLevelFeedbackMessage(DiscordDto.LevelFeedbackDiscordDto feedbackDto) {
        return DiscordDto.MessageDto.builder()
                .content("# 📚 난이도 피드백 🚨")
                .embeds(List.of(
                        DiscordDto.EmbedDto.builder()
                                .title("📰 뉴스 정보")
                                .description(
                                        "**📌 뉴스 제목:** " + feedbackDto.getNewsTitle() + "\n" +
                                                "**📅 뉴스 날짜:** " + feedbackDto.getNewsDate() + "\n" +
                                                "**🔗 출처:** " + feedbackDto.getSource() + "\n" +
                                                "**📊 사용자 체감 난이도:** " + feedbackDto.getLevel()
                                )
                                .build(),
                        DiscordDto.EmbedDto.builder()
                                .title("📖 뉴스 문단 내용")
                                .description(formatParagraphFeedback(feedbackDto.getFeedbackParagraphs()))
                                .build(),
                        DiscordDto.EmbedDto.builder()
                                .title("❓ 객관식 퀴즈 피드백")
                                .description(formatOptionQuizFeedback(feedbackDto.getFeedbackOptionQuizDtos()))
                                .build(),
                        DiscordDto.EmbedDto.builder()
                                .title("✏️ 주관식 퀴즈 피드백")
                                .description(formatContentQuizFeedback(feedbackDto.getFeedbackContentQuizzes()))
                                .build()
                )).build();
    }

    /**
     * 문단을 포맷팅하여 반환
     */
    private String formatParagraphFeedback(List<DiscordDto.FeedbackParagraphDto> paragraphs) {
        if (paragraphs == null || paragraphs.isEmpty()) {
            return "🔹 문단을 찾을 수 없습니다.";
        }
        return paragraphs.stream()
                .map(p -> "**🔹 단락 ID : " + p.getParagraphId() + "**\n```" + p.getContent() + "```")
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * 객관식 퀴즈 피드백을 포맷팅하여 반환
     */
    private String formatOptionQuizFeedback(List<DiscordDto.FeedbackOptionQuizDto> quizzes) {
        if (quizzes == null || quizzes.isEmpty()) {
            return ""; // 퀴즈가 없으면 Discord 메시지에서 해당 섹션 제외
        }
        return quizzes.stream()
                .map(q -> "**📌 퀴즈 ID: " + q.getQuizId() + "**\n"
                        + "**🔍 유형:** " + q.getType().getValue() + "\n"
                        + "**✅ 정답:** `" + q.getAnswer() + "`\n"
                        + "**💬 선택된 단어:** `" + q.getWord()+ "`\n"
                        + "**📖 관련 단락 ID:** `" + q.getSourceParagraphId() + "`\n"
                        + "**📜 보기:**\n"
                        + formatQuizOptions(q.getOptions()))
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * 객관식 퀴즈 보기 옵션을 포맷팅
     */
    private String formatQuizOptions(List<String> options) {
        if (options == null || options.isEmpty()) {
            return "🔹 보기가 없습니다.";
        }
        // 옵션을 1번부터 매핑하여 반환
        return options.stream()
                .map((o) -> (options.indexOf(o) + 1) + ". " + o)
                .collect(Collectors.joining("\n"));

    }

    /**
     * 주관식 퀴즈 피드백을 포맷팅하여 반환
     */
    private String formatContentQuizFeedback(List<DiscordDto.FeedbackContentQuizDto> quizzes) {
        if (quizzes == null || quizzes.isEmpty()) {
            return ""; // 퀴즈가 없으면 Discord 메시지에서 해당 섹션 제외
        }
        return quizzes.stream()
                .map(q -> "**📌 퀴즈 ID: " + q.getQuizId() + "**\n"
                        + "**🔍 유형:** " + q.getType().getValue() + "\n"
                        + "**❓ 질문:** " + q.getQuestion() + "\n"
                        + "**✅ 정답:** `" + (q.getAnswer() ? "O" : "X") + "`\n"
                        + "**📖 관련 단락 ID:** `" + q.getSourceParagraphId() + "`")
                .collect(Collectors.joining("\n\n"));
    }

}