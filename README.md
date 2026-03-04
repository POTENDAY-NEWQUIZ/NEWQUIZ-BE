# 📰 NEWQUIZ — 뉴스로 배우는 AI 어휘 퀴즈 서비스

> **네이버클라우드 × 비사이트 AI 포텐데이 최우수상 수상작**

뉴스 기사의 어려운 어휘를 AI가 분석하여 퀴즈를 자동 생성하고,\
사용자가 직접 기사를 요약 · 학습하며 어휘력을 키울 수 있는 서비스입니다.

<br>

## 🏆 수상 및 발표

| 구분       | 내용 |
|----------|---|
| 수상       | 네이버클라우드 × 비사이트 AI 포텐데이 **최우수상** |
| 인터뷰      | [네이버클라우드 테크 블로그](https://clova.ai/tech-blog/clova-studio%EB%A5%BC-%ED%86%B5%ED%95%B4-%EB%AC%B8%ED%95%B4%EB%A0%A5-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0%EC%97%90-%EB%8F%84%EC%A0%84%ED%95%98%EB%8B%A4%E3%85%A3ai-%ED%8F%AC%ED%85%90%EB%8D%B0%EC%9D%B4) |
| 기술 데모 발표 | [발표 영상 보기](https://www.youtube.com/watch?v=y0Plw95FAnM&t=1582s) |

<br>

## 📌 서비스 소개

청소년의 문해력, 독해력 상승을 위한\
**뉴스 사설 기사 → AI 퀴즈 자동 생성 → 요약 피드백** 파이프라인을 구축하였습니다.

사용자는 별도의 단어장이나 교재 없이, 실제 뉴스를 읽고 3가지 유형의 퀴즈를 풀며\
자연스럽게 어휘와 독해력을 향상할 수 있습니다.

<br>

## 🔄 서비스 흐름

```
[뉴스 크롤링]        네이버 뉴스 오피니언 사설을 매일 자동 수집
      ↓
[AI 카테고리 분류]   CLOVA AI → 정치 / 경제 / 사회 / 글로벌
      ↓
[AI 요약 생성]       CLOVA AI → 기사 핵심 요약 자동 생성
      ↓
[AI 퀴즈 생성]       CLOVA AI → 유의어 / 단어뜻 / 내용일치 퀴즈 자동 생성
      ↓
[사용자 학습]        뉴스 읽기 → 퀴즈 풀기 → 요약 작성 → AI 피드백
      ↓
[결과 관리]          오답노트 · 랭킹 · 학습 연속일 트래킹
```

<br>

## ✨ 주요 기능

### 1. 뉴스 자동 수집 · 분류
- Jsoup으로 네이버 뉴스 오피니언 사설 크롤링 (24개 주요 언론사 필터링)
- 문단 수 기반 난이도 자동 분류 (`상` / `하`)
- CLOVA AI로 카테고리 자동 분류 (정치 / 경제 / 사회 / 글로벌)

### 2. AI 퀴즈 자동 생성
뉴스 본문을 CLOVA AI에 전달하여 3가지 유형의 퀴즈를 자동 생성합니다.

| 유형 | 설명 |
|---|---|
| **유의어 (SYNONYM)** | 본문 속 단어의 유의어를 4지선다로 선택 |
| **단어 뜻 (MEANING)** | 문맥 속 단어의 정확한 의미를 4지선다로 선택 |
| **내용 일치 (CONTENT)** | 기사 내용을 바탕으로 O/X 판별 |

- 생성된 퀴즈에 대해 **유효성 검증 및 자동 재시도** 로직 포함
- 유효하지 않은 퀴즈/뉴스 자동 삭제 처리

### 3. AI 요약 피드백
- 사용자가 기사 문단별 요약문을 직접 작성
- CLOVA AI가 요약의 핵심 포함 여부를 분석하여 점수 및 피드백 제공

### 4. 오답노트
- 퀴즈 오답 항목을 유형별로 조회
- 오답 문제를 다시 확인하고 복습 완료 처리

### 5. 랭킹 · 학습 통계
- 퀴즈 점수 + 요약 점수 합산 누적 랭킹
- 연속 학습일 계산 및 최장 연속 기록 저장
- 최근 7일 학습 현황 그래프

### 6. 사용자 인증
- 카카오 소셜 로그인 (OAuth2)
- JWT Access Token + Redis 기반 Refresh Token
- 프로필 이미지 관리 (AWS S3)

<br>

## 🛠 기술 스택

### Backend
| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.3 |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security, OAuth2 Client |
| Auth | JWT (jjwt 0.12.3) |
| DB | MySQL |
| Cache | Redis |
| Storage | AWS S3 |
| HTTP Client | Spring WebFlux (WebClient) |
| Crawler | Jsoup 1.15.3 |
| Container | Docker |

### AI
| 분류 | 기술 |
|---|---|
| LLM | **CLOVA AI (HyperCLOVA X)** |
| 활용 | 뉴스 카테고리 분류, 퀴즈 생성, 요약 생성, 요약 피드백 |

<br>

## 🗂 프로젝트 구조

```
src/main/java/com/example/newquiz/
├── auth/                    # OAuth2 카카오 로그인, JWT 필터
├── common/
│   ├── config/              # Security, S3 설정
│   ├── util/                # JwtUtil, ClovaUtil, ImageUtil
│   ├── exception/           # 전역 예외 처리
│   └── status/              # 공통 응답 상태 코드
├── controller/              # REST API 컨트롤러
├── service/
│   ├── NewsCrawlerService   # 뉴스 크롤링 및 파이프라인 오케스트레이션
│   ├── NewsCategorizeService # AI 카테고리 분류
│   ├── QuizCreateService    # AI 퀴즈 생성 및 유효성 검증
│   ├── SummaryV2Service     # AI 요약 생성
│   ├── SummaryService       # 사용자 요약 제출 및 AI 피드백
│   ├── QuizService          # 퀴즈 조회 및 결과 저장
│   ├── NoteService          # 오답노트 조회
│   ├── RankingService       # 랭킹 조회
│   ├── UserService          # 회원 관리 및 학습 통계
│   └── TokenService         # 토큰 갱신
├── domain/                  # JPA 엔티티
│   ├── enums/               # QuizType, NewsCategory
│   └── ...
├── dto/                     # Request / Response DTO
├── repository/              # JPA Repository
└── discord/                 # 디스코드 알림 (피드백 전송)

src/main/resources/
├── quiz-generate-prompt.txt      # 퀴즈 생성 프롬프트
├── summary-generate-prompt.txt   # 요약 생성 프롬프트
├── summary-feedback-prompt.txt   # 요약 피드백 프롬프트
└── news-categorize-prompt.txt    # 카테고리 분류 프롬프트
```

<br>

## ⚙️ 실행 방법

### 1. 사전 요구사항
- Java 17
- MySQL
- Redis
- AWS S3 버킷
- Naver CLOVA AI API Key
- Kakao OAuth2 Client

### 2. 환경 변수 설정
`src/main/resources/application-secret.yml`에 아래 항목을 설정합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://{HOST}:{PORT}/{DATABASE}
    username: {DB_USERNAME}
    password: {DB_PASSWORD}
  data:
    redis:
      host: {REDIS_HOST}
      port: {REDIS_PORT}
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: {KAKAO_CLIENT_ID}
            client-secret: {KAKAO_CLIENT_SECRET}

jwt:
  secret: {JWT_SECRET}

clova:
  api-key: {CLOVA_API_KEY}
  api-gateway-key: {CLOVA_GATEWAY_KEY}

cloud:
  aws:
    credentials:
      access-key: {AWS_ACCESS_KEY}
      secret-key: {AWS_SECRET_KEY}
    s3:
      bucket: {S3_BUCKET_NAME}
    region:
      static: ap-northeast-2
```

### 3. 빌드 및 실행

```bash
# 빌드
./gradlew build

# 로컬 실행
java -jar build/libs/newquiz-0.0.1-SNAPSHOT.jar

# Docker 실행
docker build -t newquiz .
docker run -p 8080:8080 newquiz
```

<br>

## 📡 API 엔드포인트

| 도메인 | 엔드포인트 | 설명 |
|---|---|---|
| 인증 | `POST /user/register` | 회원가입 |
| 인증 | `GET /user/nickname/check` | 닉네임 중복 확인 |
| 인증 | `POST /token/refresh` | 토큰 재발급 |
| 유저 | `GET /user/mypage` | 마이페이지 조회 |
| 유저 | `GET /user/study-info` | 학습 통계 조회 |
| 유저 | `PATCH /user/nickname` | 닉네임 변경 |
| 유저 | `POST /user/profile` | 프로필 이미지 변경 |
| 뉴스 | `GET /news` | 뉴스 목록 조회 (카테고리/난이도 필터) |
| 뉴스 | `GET /news/{newsId}` | 뉴스 상세 조회 |
| 퀴즈 | `GET /quiz/{newsId}` | 퀴즈 조회 |
| 퀴즈 | `POST /quiz/result` | 퀴즈 결과 제출 |
| 요약 | `POST /summary` | 요약 제출 및 AI 피드백 수신 |
| 오답노트 | `GET /note` | 오답노트 목록 조회 |
| 오답노트 | `GET /note/{quizResultId}` | 오답노트 상세 조회 |
| 랭킹 | `GET /ranking` | 전체 랭킹 조회 |

<br>

## 👤 개발자

| 역할 | 이름 |
|---|---|
| **Backend** | 오세연 (단독 개발) |

> 뉴스 크롤링 파이프라인, CLOVA AI 연동 (퀴즈·요약·분류), 인증 시스템, 전체 API 설계 및 구현을 1인 개발로 담당했습니다.

<br>

---

> **NEWQUIZ** — 뉴스를 읽고, 어휘를 익히고, 실력을 쌓다.