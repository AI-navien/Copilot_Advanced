# GitHub Copilot Instructions (조직 단위)
- 에러 응답도 `ApiResponse` 형식 유지
- 전역 예외 처리는 `@ControllerAdvice` 활용
- 비즈니스 예외는 `IllegalArgumentException`, `IllegalStateException` 사용
## 에러 처리

- API 인증/인가는 Spring Security 활용 (해당 프로젝트에서는 생략)
- 민감 정보는 환경변수로 관리
- 비밀번호는 반드시 암호화 (예: BCrypt)
## 보안 고려사항

- 테스트 메서드명: `should_기대결과_when_조건` 형식
- 통합 테스트: `@SpringBootTest` + `@EmbeddedKafka`
- 단위 테스트: JUnit 5 + Mockito
## 테스트 작성 가이드

```
└── config/         # 설정 클래스
├── kafka/          # Kafka Producer/Consumer
├── dto/            # 요청/응답 DTO
├── entity/         # JPA 엔티티
├── repository/     # 데이터 접근 계층
├── service/        # 비즈니스 로직
├── controller/     # REST API 엔드포인트
service-name/
```
## 패키지 구조

- JSON 직렬화/역직렬화 사용
- Producer/Consumer는 각 서비스의 `kafka` 패키지에 위치
- 이벤트 클래스는 `common` 모듈에 정의
### Kafka 이벤트

- HTTP 상태 코드 적절히 사용 (201 Created, 404 Not Found 등)
- 응답은 `ApiResponse<T>` 래퍼 클래스 사용
- RESTful API 원칙 준수
### API 설계 원칙

- DTO는 `@Data`, `@Builder` 롬복 어노테이션 활용
- Repository는 Spring Data JPA의 `JpaRepository` 상속
- Service 레이어는 `@Service`와 `@Transactional` 사용
- Controller는 `@RestController`와 `@RequestMapping` 사용
### Spring Boot 규칙

- 패키지: lowercase (예: `com.example.userservice`)
- 상수: UPPER_SNAKE_CASE (예: `MAX_RETRY_COUNT`)
- 메서드명: camelCase (예: `createUser`, `getOrderById`)
- 클래스명: PascalCase (예: `UserService`, `OrderController`)
### Java 코드 스타일

## 코딩 컨벤션

- **기술 스택**: Java 17, Spring Boot 3.2, Spring Cloud Gateway, Apache Kafka, H2 Database
- **아키텍처**: Microservices Architecture
- **프로젝트명**: MSA E-Commerce Training
## 프로젝트 개요

이 파일은 GitHub Copilot이 코드 제안 시 참고하는 조직/프로젝트 단위 지침입니다.

## Quibbler MCP 사용 가이드

### 개요
Quibbler는 AI 코드 리뷰 도구로, 코드 구현 후 자동으로 품질 검증을 수행합니다. 코드 작성 완료 후 반드시 Quibbler를 통해 리뷰를 받아 코드 품질을 검증하세요.

### 사용 시점
- 새로운 기능 구현 완료 후
- 리팩토링 작업 완료 후  
- Pull Request 생성 전
- 복잡한 비즈니스 로직 작성 후

### 검증 항목
- **패턴 준수**: 프로젝트 코딩 컨벤션 및 아키텍처 패턴 준수 여부
- **코드 품질**: 불필요한 코드, 중복 로직, 개선 가능한 구조 식별
- **환각 방지**: AI가 생성한 코드의 정확성 및 실제 요구사항 부합 여부 검증
- **보안**: 잠재적 보안 취약점 탐지

### 사용 방법
1. 코드 구현 완료
2. GitHub Copilot에게 "코드 리뷰해줘" 또는 "Quibbler로 검증해줘" 요청
3. 리뷰 결과 확인 후 개선사항 적용

### 주의사항
- Quibbler는 구현 완료 **후** 사용하는 도구입니다
- 리뷰 결과는 참고용이며, 최종 판단은 개발자가 수행합니다
- 중요한 비즈니스 로직은 반드시 Quibbler 검증을 거쳐야 합니다
