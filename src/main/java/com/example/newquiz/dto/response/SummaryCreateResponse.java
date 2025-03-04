package com.example.newquiz.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryCreateResponse {

    private String totalSummary;
    private List<Summary> summaries;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Long paragraphId;
        private String aiSummary;
    }
}
