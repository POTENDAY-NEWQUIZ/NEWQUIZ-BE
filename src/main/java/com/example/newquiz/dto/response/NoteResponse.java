package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class NoteResponse {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoteListDto {
        List<NoteDto> notes;
    }
}
