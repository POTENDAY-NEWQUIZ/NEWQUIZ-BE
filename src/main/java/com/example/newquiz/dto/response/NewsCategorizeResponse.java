package com.example.newquiz.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsCategorizeResponse {
    @JsonProperty("category")
    private String category;
}
