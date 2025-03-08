package com.example.newquiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RankingUserDto {
    private Long userId;
    private Long rank;
    private String nickname;
    private Integer score;
    private String profileImageUrl;
}
