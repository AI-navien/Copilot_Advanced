# 4. 프롬프트 가이드 (Prompt Engineering)

## 학습 목표
- 효과적인 프롬프트 작성 기법 (3S 원칙) 습득
- 상황별 프롬프트 템플릿 활용법 학습

---

## 교육 내용

### 4.1 3S 원칙

효과적인 프롬프트의 핵심 원칙입니다.

| 원칙 | 설명 | 예시 |
|------|------|------|
| **Simple** | 간결하게 | 불필요한 설명 제거 |
| **Specific** | 구체적으로 | 기술 스택, 형식 명시 |
| **Structured** | 구조적으로 | 단계별, 항목별 정리 |

### 4.2 BAD vs GOOD 프롬프트

#### ❌ BAD 프롬프트
```
사용자 서비스 만들어줘
```
- 모호함
- 기술 스택 불명확
- 어떤 기능인지 모름

#### ✅ GOOD 프롬프트
```
Spring Boot 3.2 + JPA로 UserService 클래스를 만들어주세요.

요구사항:
1. createUser(CreateUserRequest): 사용자 생성
2. getUserById(Long id): ID로 조회
3. getAllUsers(): 전체 목록 조회
4. deleteUser(Long id): 사용자 삭제

규칙:
- @Service, @Transactional 사용
- UserRepository 주입 (생성자 주입)
- 예외 처리: IllegalArgumentException
```

### 4.3 상황별 프롬프트 템플릿

#### 템플릿 1: 새로운 클래스 생성

```
[기술스택]으로 [클래스명] 클래스를 만들어주세요.

역할: [클래스의 역할]

메서드:
1. [메서드명1]: [설명]
2. [메서드명2]: [설명]

의존성:
- [주입받을 클래스들]

추가 요구사항:
- [어노테이션, 규칙 등]
```

#### 템플릿 2: 기존 코드 수정

```
현재 코드에 다음 기능을 추가해주세요:

추가할 기능: [기능 설명]

조건:
- [조건1]
- [조건2]

기존 코드 영향:
- [영향받는 부분 설명]
```

#### 템플릿 3: 버그 수정

```
다음 코드에서 발생하는 [에러/문제]를 수정해주세요.

현재 동작: [현재 어떻게 동작하는지]
기대 동작: [어떻게 동작해야 하는지]

관련 정보:
- 에러 메시지: [에러 내용]
- 발생 조건: [언제 발생하는지]
```

#### 템플릿 4: 테스트 코드 생성

```
[클래스명]의 단위 테스트를 작성해주세요.

테스트 대상 메서드:
1. [메서드1]
2. [메서드2]

테스트 케이스:
- 정상 케이스: [설명]
- 예외 케이스: [설명]

사용 도구: JUnit 5 + Mockito
```

### 4.4 MSA 프로젝트 실전 프롬프트

#### Kafka Producer 생성

```
Spring Kafka로 OrderEventProducer 클래스를 만들어주세요.

역할: OrderCreatedEvent를 "order-events" 토픽으로 발행

요구사항:
- KafkaTemplate<String, String> 사용
- ObjectMapper로 JSON 직렬화
- 로깅 포함 (@Slf4j)
- 예외 처리 포함
```

#### Kafka Consumer 생성

```
Spring Kafka로 OrderEventConsumer 클래스를 만들어주세요.

역할: "order-events" 토픽에서 OrderCreatedEvent 수신

요구사항:
- @KafkaListener 사용
- groupId: "product-service"
- 수신 후 ProductService.decreaseStock() 호출
- 예외 발생 시 로깅
```

---

## 실습

### 실습 1: 3S 원칙 적용
아래 프롬프트를 3S 원칙에 맞게 개선해보세요:

**Before**:
```
재고 관리하는 서비스 만들어줘
```

**After** (작성해보세요):
```
_____________________________
```

### 실습 2: 템플릿 활용
위 템플릿을 사용하여 다음 요청을 프롬프트로 작성해보세요:
- "Product Entity에 카테고리 검색 기능 추가"

---

## 핵심 포인트
1. **3S 원칙**: Simple, Specific, Structured
2. **템플릿 활용**으로 일관된 품질
3. **맥락 제공**이 핵심 (기술 스택, 규칙, 예시)
4. **점진적 요청**: 한 번에 너무 많이 요청하지 않기

