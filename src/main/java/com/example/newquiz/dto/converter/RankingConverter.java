package com.example.newquiz.dto.converter;

import com.example.newquiz.dto.response.RankingResponse;
import com.example.newquiz.dto.response.RankingUserDto;

import java.util.List;

public class RankingConverter {
    public static RankingResponse.RankingDto toRankingDto(List<RankingUserDto> rankingList, Long userId, int myRank) {
        return RankingResponse.RankingDto.builder()
                .myUserId(userId)
                .myRank(myRank)
                .ranking(rankingList)
                .build();
    }
}
