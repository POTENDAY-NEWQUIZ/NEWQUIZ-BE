package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class RankingResponse {

    @Data
    @Builder
    @AllArgsConstructor
    public static class RankingDto {
        private Long myUserId;
        private Integer myRank;
        private List<RankingUserDto> ranking;
    }
}
