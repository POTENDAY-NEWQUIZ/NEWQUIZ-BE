package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.request.QuizRequest;
import com.example.newquiz.dto.response.QuizResponse;
import com.example.newquiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/news")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/{newsId}/quiz")
    public ResponseEntity<ApiResponse<QuizResponse.QuizListDto>> getQuizInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId) {

        QuizResponse.QuizListDto response = quizService.getQuizInfo(customUserDetails.getUserId(), newsId);
        return ApiResponse.success(SuccessStatus.GET_QUIZ_INFO_SUCCESS, response);
    }

    @PostMapping("/quiz")
    public ResponseEntity<ApiResponse<String>> saveQuizResult(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody QuizRequest.QuizResultDto quizResultDto) {

        quizService.saveQuizResult(customUserDetails.getUserId(), quizResultDto);
        return ApiResponse.success(SuccessStatus.SAVE_QUIZ_RESULT_SUCCESS);
    }

}
