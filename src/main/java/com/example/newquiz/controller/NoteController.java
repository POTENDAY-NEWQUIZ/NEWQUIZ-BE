package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.domain.enums.QuizType;
import com.example.newquiz.dto.response.NoteResponse;
import com.example.newquiz.service.NoteService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/note")
public class NoteController {
    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<ApiResponse<NoteResponse.NoteListDto>> getNoteList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("type") String type
    ) {
        NoteResponse.NoteListDto response = noteService.getNoteList(userDetails.getUserId(), QuizType.getQuizType(type));
        return ApiResponse.success(SuccessStatus.NOTE_LIST_SUCCESS, response);
    }

    @GetMapping("/{quizResultId}")
    public ResponseEntity<ApiResponse<NoteResponse.NoteDetailDto>> getNoteDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long quizResultId
    ) {
        NoteResponse.NoteDetailDto response = noteService.getNoteDetail(userDetails.getUserId(), quizResultId);
        return ApiResponse.success(SuccessStatus.NOTE_DETAIL_SUCCESS, response);
    }
}
