package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.discord.DiscordAlarmSender;
import com.example.newquiz.discord.DiscordUtil;
import com.example.newquiz.discord.converter.FeedbackConverter;
import com.example.newquiz.discord.dto.DiscordDto;
import com.example.newquiz.domain.*;
import com.example.newquiz.domain.enums.NewsCategory;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.converter.NewsConverter;
import com.example.newquiz.dto.request.LevelFeedbackRequest;
import com.example.newquiz.dto.response.NewsResponse;
import com.example.newquiz.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final CompletedNewsRepository completedNewsRepository;
    private final ParagraphRepository paragraphRepository;
    private final QuizRepository quizRepository;
    private final SynonymQuizRepository synonymQuizRepository;
    private final MeaningQuizRepository meaningQuizRepository;
    private final ContentQuizRepository contentQuizRepository;
    private final DiscordAlarmSender discordAlarmSender;

    public NewsResponse.NewsListDto getNewsList(Long userId, String category) {
        // 이미 퀴즈를 푼 뉴스는 제외
        List<CompletedNews> completedNews = completedNewsRepository.findByUserId(userId);

        // 뉴스 카테고리에 해당하는 뉴스 리스트를 가져옴
        List<News> newsList = newsRepository.findByCategory(NewsCategory.getNewsCategory(category));

        // 이미 퀴즈를 푼 뉴스는 제외
        newsList.removeIf(news -> completedNews.stream().anyMatch(completed -> completed.getNewsId().equals(news.getNewsId())));

        return NewsConverter.toNewsListDto(newsList, category);
    }

    public NewsResponse.NewsDetailDto getNewsDetail(Long newsId) {
        News news = newsRepository.findById(newsId).orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        List<Paragraph> paragraphs = paragraphRepository.findByNewsId(newsId);

        return NewsConverter.toNewsDetailDto(news, paragraphs);
    }

    public void sendLevelFeedback(Long userId, LevelFeedbackRequest.LevelFeedbackDto levelFeedbackDto) {
        // 해당 뉴스 가져오기
        News news = newsRepository.findById(levelFeedbackDto.getNewsId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        // 해당 뉴스의 문단 가져오기
        List<Paragraph> paragraphs = paragraphRepository.findByNewsId(levelFeedbackDto.getNewsId());

        // 해당 뉴스의 퀴즈 가져오기
        List<Quiz> quizzes = quizRepository.findByNewsId(levelFeedbackDto.getNewsId());

        // 퀴즈를 type별로 분류
        List<Quiz> synonymQuizzes = getQuizzesByType(quizzes, QuizType.SYNONYM);
        List<Quiz> meaningQuizzes = getQuizzesByType(quizzes, QuizType.MEANING);
        List<Quiz> contentQuizzes = getQuizzesByType(quizzes, QuizType.CONTENT);

        // 각 타입별 퀴즈를 DTO로 변환 (퀴즈가 없을 경우 빈 리스트 반환)
        List<DiscordDto.FeedbackOptionQuizDto> synonymQuizDtos = mapQuizToOptionQuizDto(synonymQuizzes, synonymQuizRepository);
        List<DiscordDto.FeedbackOptionQuizDto> meaningQuizDtos = mapQuizToOptionQuizDto(meaningQuizzes, meaningQuizRepository);
        List<DiscordDto.FeedbackContentQuizDto> contentQuizDtos = mapQuizToContentQuizDto(contentQuizzes, contentQuizRepository);

        // meaning 퀴즈가 존재하는 경우만 optionQuizDtos에 추가
        List<DiscordDto.FeedbackOptionQuizDto> optionQuizDtos = new ArrayList<>(synonymQuizDtos);
        if (!meaningQuizDtos.isEmpty()) {
            optionQuizDtos.addAll(meaningQuizDtos);
        }

        DiscordDto.LevelFeedbackDiscordDto discordDto = FeedbackConverter.toLevelFeedbackDiscordDto(
                news, levelFeedbackDto, paragraphs, optionQuizDtos, contentQuizDtos
        );

        // 메시지 생성 및 디스코드 전송
        discordAlarmSender.sendFeedbackDiscordAlarm(discordDto);
    }


    /**
     * 특정 QuizType에 해당하는 퀴즈 목록을 반환
     */
    private List<Quiz> getQuizzesByType(List<Quiz> quizzes, QuizType type) {
        return quizzes.stream()
                .filter(quiz -> quiz.getType().equals(type))
                .toList();
    }

    /**
     * Option 퀴즈(Synonym, Meaning)를 DTO로 변환
     */
    /**
     * Option 퀴즈(Synonym, Meaning)를 DTO로 변환
     * 퀴즈가 존재하지 않으면 빈 리스트 반환
     */
    private List<DiscordDto.FeedbackOptionQuizDto> mapQuizToOptionQuizDto(List<Quiz> quizzes, JpaRepository<?, Long> repository) {
        if (quizzes.isEmpty()) {
            log.info("🔹 Option 퀴즈 없음 - repository: {}", repository.getClass().getSimpleName());
            return List.of(); // 빈 리스트 반환
        }

        return quizzes.stream()
                .map(quiz -> {
                    Long relatedQuizId = null;
                    if (repository instanceof SynonymQuizRepository) {
                        relatedQuizId = quiz.getSynonymQuizId();
                    } else if (repository instanceof MeaningQuizRepository) {
                        relatedQuizId = quiz.getMeaningQuizId();
                    }

                    if (relatedQuizId == null) {
                        log.warn("⚠️ 관련 Quiz ID 없음 - quizId: {}", quiz.getQuizId());
                        return null;
                    }

                    return repository.findById(relatedQuizId)
                            .map(entity -> {
                                if (entity instanceof SynonymQuiz) {
                                    return FeedbackConverter.toFeedbackSynonymQuizDto(quiz, (SynonymQuiz) entity);
                                } else if (entity instanceof MeaningQuiz) {
                                    return FeedbackConverter.toFeedbackMeaningQuizDto(quiz, (MeaningQuiz) entity);
                                }
                                return null;
                            })
                            .orElse(null);
                })
                .filter(java.util.Objects::nonNull) // null 제거
                .toList();
    }


    /**
     * Content 퀴즈를 DTO로 변환
     * 퀴즈가 존재하지 않으면 빈 리스트 반환
     */
    private List<DiscordDto.FeedbackContentQuizDto> mapQuizToContentQuizDto(List<Quiz> quizzes, JpaRepository<ContentQuiz, Long> repository) {
        if (quizzes.isEmpty()) {
            log.info("🔹 Content 퀴즈 없음 - repository: {}", repository.getClass().getSimpleName());
            return List.of(); // 빈 리스트 반환
        }
        return quizzes.stream()
                .map(quiz -> repository.findById(quiz.getContentQuizId())
                        .map(contentQuiz -> FeedbackConverter.toFeedbackContentQuizDto(quiz, contentQuiz))
                        .orElse(null)
                )
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Transactional
    public void deleteNews(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        List<Quiz> quizzes = quizRepository.findByNewsId(newsId);
        List<Paragraph> paragraphs = paragraphRepository.findByNewsId(newsId);
        List<CompletedNews> completedNews = completedNewsRepository.findAllByNewsId(newsId);

        deleteQuizzesByType(quizzes, Quiz::getSynonymQuizId, synonymQuizRepository);
        deleteQuizzesByType(quizzes, Quiz::getMeaningQuizId, meaningQuizRepository);
        deleteQuizzesByType(quizzes, Quiz::getContentQuizId, contentQuizRepository);

        paragraphRepository.deleteAll(paragraphs);
        completedNewsRepository.deleteAll(completedNews);
        quizRepository.deleteAll(quizzes);
        newsRepository.delete(news);
    }

    private <T > void deleteQuizzesByType
            (List < Quiz > quizzes, java.util.function.Function < Quiz, Long > quizIdExtractor, JpaRepository < T, Long > repository)
    {
        List<Long> ids = quizzes.stream()
                .map(quizIdExtractor)
                .filter(java.util.Objects::nonNull)
                .toList();
        if (!ids.isEmpty()) {
            repository.deleteAllById(ids);
        }
    }

}
