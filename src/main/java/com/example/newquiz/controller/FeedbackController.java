package com.example.newquiz.controller;

import com.example.newquiz.auth.dto.CustomUserDetails;
import com.example.newquiz.common.response.ApiResponse;
import com.example.newquiz.common.status.SuccessStatus;
import com.example.newquiz.dto.request.FeedbackRequest;
import com.example.newquiz.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> sendFeedback(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FeedbackRequest.FeedbackDto feedbackDto
    ) {
        feedbackService.sendAIFeedback(userDetails.getUserId(), feedbackDto);
        return ApiResponse.success(SuccessStatus.SEND_FEEDBACK_SUCCESS);
    }
}
