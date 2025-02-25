package com.example.newquiz.service;

import com.example.newquiz.domain.News;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.repository.NewsRepository;
import com.example.newquiz.repository.ParagraphRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsCrawlerService {

    private final NewsCategorizeService newsCategorizeService;
    private final NewsRepository newsRepository;
    private final ParagraphRepository paragraphRepository;

    private static final String BASE_URL = "https://news.naver.com/opinion/editorial";
        private static final List<String> ALLOWED_SOURCES = Arrays.asList(
            "강원일보", "경기일보", "국민일보", "국제신문", "농민신문", "매일경제",
            "서울경제", "서울신문", "세계일보", "이데일리", "중앙일보", "파이낸셜뉴스",
            "한국경제", "한국일보", "헤럴드경제"
    );

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    public void crawlNews() {
        try {
            log.info("📰 뉴스 크롤링 시작...");
            Document doc = Jsoup.connect(BASE_URL).get();
            Elements newsLinks = doc.select("div.opinion_calendar_content ul.opinion_editorial_list li.opinion_editorial_item a.link");

            for (Element link : newsLinks) {
                String articleUrl = link.attr("href");
                crawlArticle(articleUrl);
            }
        } catch (Exception e) {
            log.error("❌ 뉴스 크롤링 실패", e);
        }
    }

    @Transactional
    protected void crawlArticle(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String title = doc.select("h2.media_end_head_headline").text();
            String source = doc.select("a.media_end_head_top_logo img").attr("alt");
            String dateText = doc.select("span.media_end_head_info_datestamp_time").text();
            Element articleElement = doc.selectFirst("article#dic_area");

            if (!ALLOWED_SOURCES.contains(source)) {
                // 허용되지 않은 언론사인 경우 크롤링 건너뛰기
                log.warn("🚫 허용되지 않은 언론사: {}", source);
                return;
            }

            // 날짜 포맷 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. a h:mm", Locale.KOREAN);
            LocalDateTime dateTime = LocalDateTime.parse(dateText, formatter);
            LocalDate parsedDate = dateTime.toLocalDate();

            // 뉴스 저장
            News news = News.toEntity(title, parsedDate, source);
            news = newsRepository.save(news);

            // 문단 저장 (br 태그 기준)
            if (articleElement != null) {
                List<TextNode> textNodes = articleElement.textNodes();
                int order = 1;

                for (TextNode node : textNodes) {
                    String paragraph = node.text().trim();
                    if (!paragraph.isEmpty()) {
                        Paragraph para = Paragraph.toEntity(news.getNewsId(), order++, paragraph);
                        paragraphRepository.save(para);
                    }
                }
            }

            // 뉴스 저장 후 자동으로 AI 카테고리 분류 실행 (에러 발생 시 건너뛰기)
            try {
                newsCategorizeService.categorizeNews(news.getNewsId());
            } catch (Exception e) {
                log.error("🚨 AI 카테고리 분류 실패 (뉴스 ID: {}), 원인: {}", news.getNewsId(), e.getMessage());
            }

        } catch (Exception e) {
            log.error("❌ 기사 크롤링 실패: {}", url, e);
        }
    }
}
