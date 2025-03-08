package com.example.newquiz.repository;

import com.example.newquiz.domain.QuizResult;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.response.NoteDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    boolean existsByUserIdAndQuizIdAndIsCorrect(Long userId, Long quizId, Boolean isCorrect);
    void deleteByUserId(Long userId);
    @Query("""
    SELECT new com.example.newquiz.dto.response.NoteDto(
        qr.quizResultId, 
        n.category, 
        n.title, 
        n.date, 
        q.type, 
        COALESCE(qr.isChecked, false)
    )
    FROM QuizResult qr
    JOIN Quiz q ON qr.quizId = q.quizId
    JOIN News n ON q.newsId = n.newsId
    JOIN CompletedNews cn ON n.newsId = cn.newsId AND cn.userId = :userId
    WHERE qr.userId = :userId
    AND q.type = :quizType
    AND qr.isCorrect = false
    AND cn.isCompleted = true
    ORDER BY n.date DESC
""")
    List<NoteDto> findIncorrectNotesByUserIdAndType(
            @Param("userId") Long userId,
            @Param("quizType") QuizType quizType
    );

    Optional<QuizResult> findByUserIdAndQuizResultId(Long userId, Long quizResultId);

}
