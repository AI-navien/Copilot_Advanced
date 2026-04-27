# MSA Copilot Training Project

GitHub Copilot 활용 교육을 위한 MSA(Microservices Architecture) 샘플 프로젝트입니다.

## 📚 프로젝트 개요

이 프로젝트는 간단한 E-Commerce 시스템을 MSA로 구현하여, 개발자들이 GitHub Copilot을 효과적으로 활용하는 방법을 학습할 수 있도록 설계되었습니다.

### 기술 스택
- **Language**: Java 17
- **Framework**: Spring Boot 3.2
- **Database**: H2 In-Memory
- **Messaging**: Apache Kafka
- **API Gateway**: Spring Cloud Gateway
- **Build Tool**: Gradle (Kotlin DSL)
- **Container**: Docker & Docker Compose

## 🏗️ 아키텍처

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client                                   │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                            │
└─────────────────────────────┬───────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ User Service  │    │Product Service│    │ Order Service │
│    (8081)     │    │    (8082)     │    │    (8083)     │
└───────┬───────┘    └───────┬───────┘    └───────┬───────┘
        │                    │                     │
        └────────────────────┴─────────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │     Kafka       │
                    │    (9092)       │
                    └─────────────────┘
```

### 서비스 구성

| 서비스 | 포트 | 설명 |
|--------|------|------|
| API Gateway | 8080 | 라우팅, 진입점 |
| User Service | 8081 | 사용자 관리 |
| Product Service | 8082 | 상품/재고 관리 |
| Order Service | 8083 | 주문 관리 |
| Kafka | 9092 | 이벤트 메시징 |
| Kafka UI | 8090 | Kafka 모니터링 |

### Kafka 이벤트 흐름

```
[User Service] ──UserCreatedEvent──▶ [Kafka] ──▶ [Order Service]
[Product Service] ──StockUpdatedEvent──▶ [Kafka] ──▶ [Order Service]
[Order Service] ──OrderCreatedEvent──▶ [Kafka] ──▶ [Product Service] (재고 차감)
```

## 🚀 실행 방법

### 사전 요구사항
- Java 17+
- Docker & Docker Compose
- Gradle 8.x (또는 Gradle Wrapper 사용)

### 로컬 실행 (개발 모드)

1. **Kafka 실행** (Docker)
```bash
docker-compose up -d zookeeper kafka kafka-ui
```

2. **각 서비스 실행**
```bash
# 터미널 1: User Service
./gradlew :user-service:bootRun

# 터미널 2: Product Service
./gradlew :product-service:bootRun

# 터미널 3: Order Service
./gradlew :order-service:bootRun

# 터미널 4: API Gateway
./gradlew :api-gateway:bootRun
```

### Docker Compose 전체 실행

```bash
# 전체 빌드 및 실행
docker-compose up --build

# 백그라운드 실행
docker-compose up -d --build

# 종료
docker-compose down
```

## 📖 교육 자료

`docs/` 폴더에 8단계 교육 자료가 준비되어 있습니다:

| 단계 | 문서 | 내용 |
|------|------|------|
| 1 | [01-problem-definition.md](docs/01-problem-definition.md) | 요구사항 정의 |
| 2 | [02-instruction-guide.md](docs/02-instruction-guide.md) | Copilot Instruction 활용 |
| 3 | [03-development-spec.md](docs/03-development-spec.md) | 개발 정의서 작성 |
| 4 | [04-prompt-guide.md](docs/04-prompt-guide.md) | 효과적인 프롬프트 작성 |
| 5 | [05-code-reading-guide.md](docs/05-code-reading-guide.md) | AI 코드 이해하기 |
| 6 | [06-code-review-guide.md](docs/06-code-review-guide.md) | 코드 검증/리뷰 |
| 7 | [07-testing-guide.md](docs/07-testing-guide.md) | 테스트 코드 작성 |
| 8 | [08-refactoring-guide.md](docs/08-refactoring-guide.md) | 리팩토링 |

## 🛠️ API 엔드포인트

### User Service
```
POST   /api/users          - 사용자 생성
GET    /api/users          - 전체 사용자 조회
GET    /api/users/{id}     - 단일 사용자 조회
DELETE /api/users/{id}     - 사용자 삭제
```

### Product Service
```
POST   /api/products                    - 상품 생성
GET    /api/products                    - 전체 상품 조회
GET    /api/products/{id}               - 단일 상품 조회
GET    /api/products/category/{cat}     - 카테고리별 조회
PATCH  /api/products/{id}/stock/increase - 재고 증가
DELETE /api/products/{id}               - 상품 삭제
```

### Order Service
```
POST   /api/orders                  - 주문 생성
GET    /api/orders                  - 전체 주문 조회
GET    /api/orders/{id}             - 단일 주문 조회
GET    /api/orders/user/{userId}    - 사용자별 주문 조회
PATCH  /api/orders/{id}/status      - 주문 상태 변경
POST   /api/orders/{id}/cancel      - 주문 취소
```

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 서비스 테스트
./gradlew :user-service:test
./gradlew :product-service:test
./gradlew :order-service:test
```

## 📁 프로젝트 구조

```
msa-copilot-training/
├── .github/
│   └── copilot-instructions.md    # Copilot 조직 단위 설정
├── docs/                           # 교육 자료
├── common/                         # 공통 모듈 (DTO, Event)
├── user-service/                   # 사용자 서비스
├── product-service/                # 상품 서비스
├── order-service/                  # 주문 서비스
├── api-gateway/                    # API Gateway
├── docker-compose.yml              # Docker 설정
├── build.gradle.kts                # 루트 빌드 설정
└── settings.gradle.kts             # 멀티모듈 설정
```

## 🎯 학습 목표

이 프로젝트를 통해 다음을 학습할 수 있습니다:

1. **Copilot Instruction** 활용으로 일관된 코드 품질 확보
2. **3S 원칙** (Simple, Specific, Structured) 기반 프롬프트 작성
3. **AI 생성 코드** 검증 및 리뷰 방법
4. **MSA 환경**에서의 테스트 전략
5. **Kafka 이벤트 기반** 서비스 간 통신

## 📝 라이선스

This project is for educational purposes.

