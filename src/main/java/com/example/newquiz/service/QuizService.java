package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.domain.ContentQuiz;
import com.example.newquiz.domain.MeaningQuiz;
import com.example.newquiz.domain.Quiz;
import com.example.newquiz.domain.SynonymQuiz;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.converter.QuizConverter;
import com.example.newquiz.dto.response.QuizResponse;
import com.example.newquiz.repository.ContentQuizRepository;
import com.example.newquiz.repository.MeaningQuizRepository;
import com.example.newquiz.repository.QuizRepository;
import com.example.newquiz.repository.SynonymQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final SynonymQuizRepository synonymQuizRepository;
    private final ContentQuizRepository contentQuizRepository;
    private final MeaningQuizRepository meaningQuizRepository;

    public QuizResponse.QuizListDto getQuizInfo(Long userId, Long newsId) {

        // 퀴즈가 있는지 확인
        checkExistQuiz(newsId);

        // 퀴즈가 있으면 Quiz 테이블에서 카테고리별로 가져옴
        List<Quiz> sysnonymList = quizRepository.findByNewsIdAndType(newsId, QuizType.SYNONYM);
        List<Quiz> meaningList = quizRepository.findByNewsIdAndType(newsId, QuizType.MEANING);
        List<Quiz> contentList = quizRepository.findByNewsIdAndType(newsId, QuizType.CONTENT);

        // 카테고리별로 DTO 생성
        List<QuizResponse.SynonymQuizDto> synonymQuizDto = createSynonymQuizDto(sysnonymList);
        List<QuizResponse.MeaningQuizDto> meaningQuizDto = createMeaningQuizDto(meaningList);
        List<QuizResponse.ContentQuizDto> contentQuizDto = createContentQuizDto(contentList);

        // QuizListDto에 추가
        return QuizConverter.toQuizListDto(sysnonymList.size(), meaningList.size(), contentList.size(), synonymQuizDto, meaningQuizDto, contentQuizDto);

        // 리턴
    }

    private void checkExistQuiz(Long newsId) {
        // 퀴즈가 있는지 확인
        if (!quizRepository.existsByNewsId(newsId)) {
            log.warn("뉴스 ID {}에 대한 퀴즈가 없습니다.", newsId);
            throw new GeneralException(ErrorStatus.NOT_FOUND_QUIZ);
        }
    }

    private List<QuizResponse.SynonymQuizDto> createSynonymQuizDto(List<Quiz> synonymList) {
        // synonymList에서 quizId 가져오기
        List<Long> synonymQuizIds = synonymList.stream().map(Quiz::getQuizId).collect(Collectors.toList());

        // SynonymQuiz와 Quiz 엔티티 가져오기
        List<SynonymQuiz> synonymQuizList = synonymQuizRepository.findAllByQuizId(synonymQuizIds);

        // DTO 변환
        return QuizConverter.toSynonymQuizDtoList(synonymQuizList, synonymList);
    }

    private List<QuizResponse.MeaningQuizDto> createMeaningQuizDto(List<Quiz> meaningList) {
        // meaningList에서 quizId 가져오기
        List<Long> meaningQuizIds = meaningList.stream().map(Quiz::getQuizId).collect(Collectors.toList());

        // meaningQuiz와 Quiz 엔티티 가져오기
        List<MeaningQuiz> meaningQuizList = meaningQuizRepository.findAllByQuizId(meaningQuizIds);

        // DTO 변환
        return QuizConverter.toMeaningQuizDtoList(meaningQuizList, meaningList);
    }

    private List<QuizResponse.ContentQuizDto> createContentQuizDto(List<Quiz> contentList) {
        // contentList에서 quizId 가져오기
        List<Long> contentQuizIds = contentList.stream().map(Quiz::getQuizId).collect(Collectors.toList());

        // contentQuiz와 Quiz 엔티티 가져오기
        List<ContentQuiz> contentQuizList = contentQuizRepository.findAllByQuizId(contentQuizIds);

        // DTO 변환
        return QuizConverter.toContentQuizDtoList(contentQuizList, contentList);
    }

}
