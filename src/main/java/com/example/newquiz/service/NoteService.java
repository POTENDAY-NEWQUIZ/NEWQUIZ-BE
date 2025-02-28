package com.example.newquiz.service;

import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.response.NoteDto;
import com.example.newquiz.dto.response.NoteResponse;
import com.example.newquiz.repository.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
    private final QuizResultRepository quizResultRepository;

    public NoteResponse.NoteListDto getNoteList(Long userId, QuizType quizType) {
        List<NoteDto> notes = quizResultRepository.findIncorrectNotesByUserIdAndType(userId, quizType);
        return new NoteResponse.NoteListDto(notes);
    }
}
