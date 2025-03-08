package com.example.newquiz.service;

import com.example.newquiz.dto.converter.RankingConverter;
import com.example.newquiz.dto.response.RankingResponse;
import com.example.newquiz.dto.response.RankingUserDto;
import com.example.newquiz.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingRepository rankingRepository;

    public RankingResponse.RankingDto getRanking(Long userId) {
        List<RankingUserDto> rankingList = rankingRepository.findAllRankingsWithUserDetails();

        int myRank = 0;
        for (int i = 0; i < rankingList.size(); i++) {
            if (rankingList.get(i).getUserId().equals(userId)) {
                myRank = i + 1;
                break;
            }
        }

        return RankingConverter.toRankingDto(rankingList, userId, myRank);
    }
}
