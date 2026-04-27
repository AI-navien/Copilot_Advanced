# GitHub Issue 내용

아래 내용을 GitHub Issue로 생성하세요.
**URL**: https://github.com/ilmechaJu/Copilot-Advanced-MSA-Java/issues/new

---

## Issue 제목
```
[FEATURE] MSA E-Commerce 프로젝트 초기 구축
```

## Issue 본문

```markdown
## 🎯 기능 설명
MSA(Microservices Architecture) 기반 E-Commerce 교육용 프로젝트의 초기 구축

## 💡 제안 배경
- Java 17, Spring Boot 3.2, Apache Kafka를 활용한 MSA 학습 환경 구축
- 마이크로서비스 간 이벤트 기반 통신 구현
- Docker Compose를 통한 로컬 개발 환경 구성

## 📝 상세 요구사항

### 서비스 구성
- [x] **User Service** (8081) - 사용자 관리
- [x] **Product Service** (8082) - 상품 관리  
- [x] **Order Service** (8083) - 주문 관리
- [x] **API Gateway** (8080) - Spring Cloud Gateway

### 공통 모듈
- [x] **common** - 공통 DTO (ApiResponse) 및 Kafka 이벤트 클래스

### 인프라
- [x] Zookeeper + Kafka 설정
- [x] Kafka UI (8090) 모니터링
- [x] Docker Compose 구성
- [x] 멀티 모듈 Gradle 프로젝트 구조

### Kafka 이벤트
- [x] `UserCreatedEvent` - 사용자 생성 이벤트
- [x] `OrderCreatedEvent` - 주문 생성 이벤트
- [x] `StockUpdatedEvent` - 재고 업데이트 이벤트

### 테스트
- [x] 단위 테스트 (JUnit 5 + Mockito)
- [x] 테스트 명명 규칙: `should_기대결과_when_조건`

### 문서화
- [x] GitHub Issue/PR 템플릿
- [x] Copilot Instructions

## 📎 기술 스택
- Java 17
- Spring Boot 3.2.1
- Spring Cloud Gateway
- Apache Kafka
- H2 Database
- Docker & Docker Compose
- Gradle (Kotlin DSL)
```

## Labels
- `enhancement`
- `documentation`

