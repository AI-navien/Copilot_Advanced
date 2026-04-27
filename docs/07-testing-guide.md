# 7. 테스트 가이드 (Testing Guide)

## 학습 목표
- Copilot을 활용한 테스트 코드 작성법 습득
- 단위 테스트와 통합 테스트 구현 방법 학습
- MSA 환경에서의 테스트 전략 이해

---

## 교육 내용

### 7.1 테스트 피라미드

```
        /\
       /  \     E2E 테스트 (적음)
      /----\
     /      \   통합 테스트 (중간)
    /--------\
   /          \  단위 테스트 (많음)
  --------------
```

| 테스트 유형 | 범위 | 속도 | 비용 |
|-------------|------|------|------|
| 단위 테스트 | 클래스/메서드 | 빠름 | 낮음 |
| 통합 테스트 | 서비스 내부 | 중간 | 중간 |
| E2E 테스트 | 전체 시스템 | 느림 | 높음 |

### 7.2 단위 테스트 작성

#### Copilot 프롬프트

```
UserService.createUser() 메서드의 단위 테스트를 작성해주세요.

테스트 케이스:
1. 정상적으로 사용자가 생성되는 경우
2. 이미 존재하는 username인 경우 예외 발생
3. 이미 존재하는 email인 경우 예외 발생

사용 도구: JUnit 5, Mockito
테스트 메서드명 형식: should_결과_when_조건
```

#### 예시 코드

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserEventProducer userEventProducer;
    
    @InjectMocks
    private UserService userService;

    @Test
    void should_CreateUser_when_ValidRequest() {
        // given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        User savedUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // when
        UserResponse response = userService.createUser(request);
        
        // then
        assertThat(response.getUsername()).isEqualTo("testuser");
        verify(userEventProducer).sendUserCreatedEvent(any());
    }

    @Test
    void should_ThrowException_when_UsernameExists() {
        // given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("existinguser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // when & then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 사용자명");
    }
}
```

### 7.3 Kafka 통합 테스트

#### @EmbeddedKafka 사용

```java
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
    topics = {"user-events", "order-events", "stock-events"}
)
class KafkaIntegrationTest {

    @Autowired
    private UserEventProducer userEventProducer;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void should_SendUserCreatedEvent_when_UserCreated() {
        // given
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .build();
        
        // when
        userEventProducer.sendUserCreatedEvent(event);
        
        // then
        // Consumer로 메시지 수신 확인
    }
}
```

### 7.4 MSA 통합 테스트 전략

#### 서비스별 독립 테스트
```
각 서비스를 독립적으로 테스트
- Mock으로 외부 서비스 대체
- @EmbeddedKafka로 Kafka 테스트
```

#### 컨트랙트 테스트
```
서비스 간 API 계약 검증
- Spring Cloud Contract 활용
- Provider/Consumer 양측 테스트
```

#### Docker Compose 통합 테스트
```
전체 시스템 E2E 테스트
- Testcontainers 활용
- 실제 환경과 유사한 테스트
```

### 7.5 테스트 커버리지 확인

#### Copilot으로 누락된 테스트 케이스 찾기

```
UserService 클래스의 테스트 커버리지를 분석하고
누락된 테스트 케이스를 알려주세요.

현재 테스트:
- 정상 생성
- 중복 username

추가로 필요한 테스트 케이스는?
```

#### 예상 응답
- 중복 email 테스트
- getUserById 정상 케이스
- getUserById 존재하지 않는 ID
- deleteUser 정상 케이스
- deleteUser 존재하지 않는 ID
- getAllUsers 빈 목록
- getAllUsers 여러 사용자

---

## 실습

### 실습 1: 단위 테스트 작성
`ProductService.decreaseStock()` 메서드의 단위 테스트를 작성하세요.

**테스트 케이스**:
1. 정상적으로 재고가 감소하는 경우
2. 재고가 부족한 경우 예외 발생
3. 존재하지 않는 상품인 경우 예외 발생

### 실습 2: Copilot으로 테스트 생성
Copilot Chat에 다음 프롬프트를 입력하세요:

```
OrderService 클래스의 전체 메서드에 대한
단위 테스트 코드를 생성해주세요.
JUnit 5 + Mockito 사용
각 테스트 메서드에 주석으로 테스트 의도 설명 포함
```

---

## 핵심 포인트
1. **테스트 피라미드**: 단위 > 통합 > E2E
2. **Mockito 활용**: 외부 의존성 격리
3. **@EmbeddedKafka**: Kafka 통합 테스트
4. **Copilot 활용**: 테스트 케이스 생성 가속화

