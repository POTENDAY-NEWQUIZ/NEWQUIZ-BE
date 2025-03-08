package com.example.newquiz.service;

import com.example.newquiz.common.exception.GeneralException;
import com.example.newquiz.common.status.ErrorStatus;
import com.example.newquiz.domain.*;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.converter.NewsConverter;
import com.example.newquiz.dto.converter.QuizConverter;
import com.example.newquiz.dto.response.NewsResponse;
import com.example.newquiz.dto.response.NoteDto;
import com.example.newquiz.dto.response.NoteResponse;
import com.example.newquiz.dto.response.QuizResponse;
import com.example.newquiz.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    private final QuizResultRepository quizResultRepository;
    private final QuizRepository quizRepository;
    private final NewsRepository newsRepository;
    private final ParagraphRepository paragraphRepository;
    private final SynonymQuizRepository synonymQuizRepository;
    private final MeaningQuizRepository meaningQuizRepository;
    private final ContentQuizRepository contentQuizRepository;

    public NoteResponse.NoteListDto getNoteList(Long userId, QuizType quizType) {
        List<NoteDto> notes = quizResultRepository.findIncorrectNotesByUserIdAndType(userId, quizType);
        return new NoteResponse.NoteListDto(notes);
    }

    public NoteResponse.NoteDetailDto getNoteDetail(Long userId, Long quizResultId) {
        QuizResult quizResult = quizResultRepository.findByUserIdAndQuizResultId(userId, quizResultId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.QUIZ_RESULT_NOT_FOUND));

        Quiz resultQuiz = quizRepository.findById(quizResult.getQuizId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.QUIZ_NOT_FOUND));

        News news = newsRepository.findById(resultQuiz.getNewsId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));

        List<Paragraph> paragraphs = paragraphRepository.findByNewsIdOrderByContentOrderAsc(news.getNewsId());

        NewsResponse.NewsDetailDto newsDetailDto = NewsConverter.toNewsDetailDto(news, paragraphs);

        switch (resultQuiz.getType()) {
            case SYNONYM:
                SynonymQuiz synonymQuiz = synonymQuizRepository.findById(resultQuiz.getQuizId())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
                QuizResponse.SynonymQuizDto synonymQuizDto = QuizConverter.toSynonymQuizDto(synonymQuiz, resultQuiz);
                return new NoteResponse.NoteDetailDto(quizResult.getQuizResultId(), newsDetailDto, synonymQuizDto, null, null);
            case MEANING:
                MeaningQuiz meaningQuiz = meaningQuizRepository.findById(resultQuiz.getQuizId())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
                QuizResponse.MeaningQuizDto meaningQuizDto = QuizConverter.toMeaningQuizDto(meaningQuiz, resultQuiz);
                return new NoteResponse.NoteDetailDto(quizResult.getQuizResultId(), newsDetailDto, null, meaningQuizDto, null);
            case CONTENT:
                ContentQuiz contentQuiz = contentQuizRepository.findById(resultQuiz.getQuizId())
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
                QuizResponse.ContentQuizDto contentQuizDto = QuizConverter.toContentQuizDto(contentQuiz, resultQuiz);
                return new NoteResponse.NoteDetailDto(quizResult.getQuizResultId(), newsDetailDto, null, null, contentQuizDto);
            default:
                throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

    }
}
