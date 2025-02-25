package com.example.newquiz.controller;

import com.example.newquiz.service.NewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsCrawlerController {

    private final NewsCrawlerService newsCrawlerService;

    @PostMapping("/crawl")
    public String crawlNews() {
        newsCrawlerService.crawlNews();
        return "테스트 크롤링 완료!";
    }
}
