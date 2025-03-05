package com.example.newquiz.service;

import com.example.newquiz.domain.News;
import com.example.newquiz.domain.Paragraph;
import com.example.newquiz.repository.NewsRepository;
import com.example.newquiz.repository.ParagraphRepository;
import com.example.newquiz.repository.QuizRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsCrawlerService {

    private final QuizCreateService quizCreateService;
    private final NewsCategorizeService newsCategorizeService;
    private final NewsRepository newsRepository;
    private final ParagraphRepository paragraphRepository;
    private final SummaryV2Service summaryV2Service;

    private static final String BASE_URL = "https://news.naver.com/opinion/editorial";
    private static final List<String> ALLOWED_SOURCES = Arrays.asList(
            "강원일보", "경기일보", "경향신문", "국민일보", "국제신문", "농민신문",
            "대전일보", "동아일보", "디지털타임스", "매일경제", "매일신문", "문화일보",
            "부산일보", "서울경제", "서울신문", "이데일리", "조선일보",
            "중앙일보", "파이낸셜뉴스", "한겨레", "한국경제", "한국일보", "헤럴드경제",
            "강원도민일보"
    );


    @Scheduled(cron = "0 45 23 * * ?") // 테스트 중
    public void crawlNews() {
        try {
            log.info("📰 뉴스 크롤링 시작...");
            Document doc = Jsoup.connect(BASE_URL).get();
            Elements newsLinks = doc.select("div.opinion_calendar_content ul.opinion_editorial_list li.opinion_editorial_item a.link");
            log.info("🔗 크롤링된 뉴스 링크 수: {}", newsLinks.size());
            int i = 0;
            for (Element link : newsLinks) {
                log.info("크롤링 시도 횟수 : {}", i++);
                String articleUrl = link.attr("href");
                crawlArticle(articleUrl);
            }
        } catch (Exception e) {
            log.error("❌ 뉴스 크롤링 실패 원인 : {}", e.getMessage());
        }
    }


    protected void crawlArticle(String url) {
        try {
            Document doc = Jsoup
                    .connect(url)
                    .timeout(50000) // 50초
                    .get();
            String title = doc.select("h2.media_end_head_headline").text();
            String source = doc.select("a.media_end_head_top_logo img").attr("alt");
            String dateText = doc.select("span.media_end_head_info_datestamp_time").attr("data-date-time");
            Element articleElement = doc.selectFirst("article#dic_area");

            if (!ALLOWED_SOURCES.contains(source)) {
                log.warn("🚫 허용되지 않은 언론사: {}", source);
                return;
            }

            LocalDate parsedDate = parseDate(dateText);
            Long newsId = saveNewsWithParagraphs(title, parsedDate, source, articleElement);
            if(newsId != null) {
                handlePostProcessing(newsId);
            }

        } catch (Exception e) {
            log.error("❌ 기사 크롤링 실패 원인 : {}", e.getMessage());
        }
    }

    /**
     * 날짜 문자열을 LocalDate로 변환
     */
    private LocalDate parseDate(String dateText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateText, formatter);
        return dateTime.toLocalDate();
    }

    /**
     * 뉴스 및 문단 저장
     */
    @Transactional
    protected Long saveNewsWithParagraphs(String title, LocalDate date, String source, Element articleElement) {
        try {
            News news = News.toEntity(title, date, source);
            news = newsRepository.save(news);
            saveParagraphs(news, articleElement);
            return news.getNewsId();

        } catch (Exception e) {
            log.error("🚨 뉴스 저장 실패 원인 : {}", e.getMessage());
            return null;
        }
    }

    /**
     * 문단 저장 및 뉴스 난이도 저장
     */
    private void saveParagraphs(News news, Element articleElement) {
        List<TextNode> textNodes = articleElement.textNodes();
        int order = 1;

        for (TextNode node : textNodes) {
            String paragraph = node.text().trim();
            if (!paragraph.isEmpty()) {
                Paragraph para = Paragraph.toEntity(news.getNewsId(), order++, paragraph);
                paragraphRepository.save(para);
            }
        }

        if (order >= 5) {
            news.setLevel("상");
        } else {
            news.setLevel("하");
        }
    }



    /**
     * AI 분류 및 퀴즈 생성 처리
     */
    protected void handlePostProcessing(Long newsId) {
        try {
            newsCategorizeService.categorizeNews(newsId);
        } catch (Exception e) {
            log.error("🚨 AI 카테고리 분류 실패 원인: {}", e.getMessage());
            return;
        }

        try {
            summaryV2Service.saveSummary(newsRepository.findById(newsId).get());
        } catch (Exception e) {
            log.error("🚨 AI 요약 생성 실패 원인: {}", e.getMessage());
            return;
        }

        try {
            quizCreateService.createQuiz(newsId);
        } catch (Exception e) {
            log.error("🚨 퀴즈 생성 실패 원인: {}", e.getMessage());
            return;
        }
    }


}
