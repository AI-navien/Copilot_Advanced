# Pre-Course: Copilot 사용 전 필수 가이드

## 학습 목표
- GitHub Copilot 사용 시 보안 및 개인정보 보호 기준 이해
- AI 생성 코드의 리스크를 인지하고 대응 전략 습득
- 안전하고 책임감 있는 Copilot 활용 마인드셋 확립

---

## 교육 내용

### Part 1. 보안 및 개인정보 가이드라인

#### 1.1 왜 중요한가?

GitHub Copilot은 프롬프트로 전달된 내용을 기반으로 코드를 생성합니다.
**프롬프트에 포함된 정보는 외부 AI 모델에 전송될 수 있으므로**, 민감 정보를 입력하지 않는 것이 가장 중요한 보안 원칙입니다.

> ⚠️ Copilot에 입력한 프롬프트는 코드 생성을 위해 클라우드 AI 모델로 전송됩니다.
> 한 번 전송된 데이터는 되돌릴 수 없습니다.

#### 1.2 입력 금지 항목

| 분류 | 구체적 항목 | 예시 |
|------|------------|------|
| **인증 정보** | API Key, Secret Key, Token | `sk-proj-abc123...`, `Bearer eyJhbG...` |
| **비밀번호** | DB 비밀번호, 서버 접속 정보 | `password=admin1234`, SSH 키 |
| **사내 기밀 로직** | 핵심 비즈니스 알고리즘, 요금 계산식 | 자사 고유 가격 산정 공식, 보안 필터링 로직 |
| **개인정보** | 고객 이름, 연락처, 주소, 주민번호 | 실제 고객 데이터를 예시로 사용 |
| **인프라 정보** | 내부 IP, 서버 주소, DB 접속 URL | `jdbc:mysql://10.0.1.5:3306/prod_db` |
| **내부 시스템 정보** | 내부 도메인, VPN 설정, 방화벽 규칙 | `*.internal.company.co.kr` |

#### 1.3 안전한 프롬프트 작성법

##### ❌ 위험한 프롬프트

```
다음 DB에 연결하는 코드를 작성해줘.
URL: jdbc:mysql://192.168.1.100:3306/navien_prod
username: admin
password: N@vien2024!
API Key: sk-proj-abcdefg123456
```

##### ✅ 안전한 프롬프트

```
Spring Boot에서 MySQL에 연결하는 설정을 작성해줘.
- 접속 정보는 환경변수(SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, 
  SPRING_DATASOURCE_PASSWORD)에서 읽도록 설정
- application.yml에서 ${} 플레이스홀더 사용
```

##### ❌ 기밀 로직 노출

```
우리 회사 보일러 가격 산정 공식은 다음과 같아:
기본가 = 모델별단가 × (1 + 지역할증률) - 대리점할인율
여기에 할부 이자 계산은 ...
이 로직으로 PriceCalculator 클래스 만들어줘.
```

##### ✅ 추상화된 프롬프트

```
상품 가격 계산 서비스 클래스를 만들어줘.
- 기본가, 할증률, 할인율을 파라미터로 받아 최종 가격 계산
- Strategy 패턴을 사용하여 계산 로직을 교체 가능하게 설계
- 구체적인 계산식은 별도 구현 예정이므로 인터페이스만 정의
```

#### 1.4 조직 차원 보안 체크리스트

프롬프트 작성 전 아래 항목을 반드시 확인하세요:

- [ ] 프롬프트에 실제 인증 정보(API Key, 비밀번호)가 포함되어 있지 않은가?
- [ ] 실제 고객 데이터 대신 더미 데이터를 사용했는가?
- [ ] 사내 기밀 비즈니스 로직이 구체적으로 노출되지 않았는가?
- [ ] 내부 인프라 정보(IP, 도메인, 접속 URL)가 포함되어 있지 않은가?
- [ ] 민감 정보는 환경변수나 설정 파일로 분리하도록 요청했는가?

#### 1.5 `.env` 및 `.gitignore` 활용

Copilot이 생성한 코드에 하드코딩된 민감 정보가 포함될 수 있습니다.
반드시 환경변수 분리와 `.gitignore` 설정을 확인하세요.

```bash
# .gitignore에 반드시 포함
.env
*.key
*.pem
application-local.yml
```

```yaml
# application.yml - 안전한 설정 예시
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
```

---

### Part 2. AI 생성 코드 수용 리스크 관리

#### 2.1 왜 검증이 필요한가?

GitHub Copilot은 강력한 코드 생성 도구이지만, **생성된 코드를 무조건 신뢰해서는 안 됩니다.**
AI 모델의 특성상 다음과 같은 문제가 발생할 수 있습니다.

#### 2.2 Hallucination(환각) 실제 사례

##### 사례 1: 존재하지 않는 API/메서드 생성

```java
// Copilot이 생성한 코드
import org.springframework.kafka.core.KafkaTemplate;

kafkaTemplate.sendAndWait("topic", message);  // ❌ sendAndWait는 존재하지 않는 메서드
```

**실제 올바른 코드:**
```java
kafkaTemplate.send("topic", message).get();  // ✅ send() 후 Future.get()으로 대기
```

> 💡 Copilot은 메서드 이름을 "그럴듯하게" 생성하지만, 실제 API에 존재하지 않을 수 있습니다.
> **반드시 공식 문서나 IDE 자동완성으로 메서드 존재 여부를 확인하세요.**

##### 사례 2: 잘못된 설정 값 생성

```yaml
# Copilot이 생성한 application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      auto-offset-reset: newest     # ❌ 'newest'는 유효하지 않은 값
```

**올바른 설정:**
```yaml
spring:
  kafka:
    consumer:
      auto-offset-reset: latest     # ✅ earliest, latest, none 중 하나
```

##### 사례 3: 부정확한 비즈니스 로직

```java
// "재고 차감" 요청에 대해 Copilot이 생성한 코드
public void decreaseStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("상품 없음"));
    product.setStock(product.getStock() - quantity);  // ❌ 음수 재고 가능!
    productRepository.save(product);
}
```

**안전한 코드:**
```java
public void decreaseStock(Long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
    if (product.getStock() < quantity) {
        throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + product.getStock());
    }
    product.setStock(product.getStock() - quantity);
    productRepository.save(product);
}
```

#### 2.3 오래된 라이브러리/API 버전 제안 문제

Copilot의 학습 데이터에는 과거 버전의 코드가 포함되어 있어, **더 이상 권장되지 않는(deprecated) API**를 제안할 수 있습니다.

| 문제 유형 | Copilot 제안 (오래된) | 올바른 방법 (현재) |
|-----------|----------------------|-------------------|
| Spring Security 설정 | `WebSecurityConfigurerAdapter` 상속 | `SecurityFilterChain` Bean 등록 |
| Java Date 처리 | `new Date()`, `SimpleDateFormat` | `LocalDateTime`, `DateTimeFormatter` |
| JUnit 테스트 | `@RunWith(SpringRunner.class)` | `@ExtendWith(SpringExtension.class)` |
| Kafka Listener | `@KafkaListener(topics = "topic")` 단순 설정 | `ConsumerConfig` 세부 설정 포함 |
| Spring Boot 속성 | `server.servlet.context-path` | Spring Boot 3.x 속성명 변경 확인 필요 |

##### 확인 방법

```
Copilot에게 질문:
"이 코드에서 사용된 API가 Spring Boot 3.2 / Java 17 기준으로 
deprecated된 것은 없는지 확인해줘."
```

#### 2.4 AI 코드 수용 3단계 프로세스

Copilot이 생성한 코드를 수용할 때, 다음 3단계를 반드시 거치세요:

```
┌─────────────────────────────────────────────────────┐
│  Step 1. 이해 (Understand)                           │
│  - 코드가 무엇을 하는지 한 줄씩 이해                    │
│  - 모르는 API/메서드가 있으면 공식 문서 확인              │
├─────────────────────────────────────────────────────┤
│  Step 2. 검증 (Verify)                               │
│  - 컴파일/빌드 성공 여부 확인                           │
│  - 존재하지 않는 메서드/클래스 확인                      │
│  - 비즈니스 로직 정확성 검증                            │
│  - deprecated API 여부 확인                           │
├─────────────────────────────────────────────────────┤
│  Step 3. 테스트 (Test)                               │
│  - 정상 케이스 테스트                                  │
│  - 예외/경계값 테스트                                  │
│  - 기존 테스트 깨지지 않는지 확인                        │
└─────────────────────────────────────────────────────┘
```

#### 2.5 실무에서 자주 발생하는 리스크 요약

| 리스크 | 발생 원인 | 대응 방법 |
|--------|----------|----------|
| 존재하지 않는 메서드 | AI의 패턴 기반 추론 | IDE 자동완성 및 공식 문서 확인 |
| Deprecated API 사용 | 학습 데이터의 시점 차이 | 사용 기술 스택 버전 명시 후 재확인 |
| 보안 취약점 포함 | SQL Injection, XSS 등 미고려 | 보안 체크리스트 기반 코드 리뷰 |
| 불완전한 예외 처리 | 해피 패스 위주 생성 | 예외 시나리오 명시적 요청 |
| 하드코딩된 민감 정보 | 예시 코드 스타일 생성 | 환경변수 분리 요청 |
| 라이선스 이슈 | 오픈소스 코드 패턴 학습 | 라이선스 호환성 확인 |

---

## 실습

### 실습 1: 안전한 프롬프트 작성

아래의 **위험한 프롬프트**를 **안전한 프롬프트**로 변환해보세요:

```
위험한 프롬프트:
"AWS S3에 파일 업로드하는 코드 만들어줘.
 Access Key: AKIA1234567890EXAMPLE
 Secret Key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
 Bucket: navien-prod-documents
 Region: ap-northeast-2"
```

**과제:** 민감 정보를 제거하고 환경변수를 활용하도록 프롬프트를 재작성하세요.

### 실습 2: Hallucination 탐지

다음 Copilot 생성 코드에서 문제점을 찾아보세요:

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(CreateUserRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이메일 중복");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());  // 평문 저장?
        user.setCreatedAt(new Date());             // java.util.Date 사용?
        
        return userRepository.save(user);
    }
}
```

**과제:**
1. 보안 관점에서 문제점을 찾아보세요 (비밀번호 처리)
2. Deprecated API 사용 여부를 확인하세요 (Date 클래스)
3. 예외 처리 관점에서 개선점을 찾아보세요 (RuntimeException)
4. Copilot에게 개선된 코드를 요청하는 프롬프트를 작성해보세요

---

## 핵심 포인트

1. **입력 금지 항목 숙지**: API Key, 비밀번호, 사내 기밀 로직은 절대 프롬프트에 포함하지 않기
2. **환경변수 분리 원칙**: 민감 정보는 반드시 환경변수 또는 외부 설정으로 관리
3. **맹목적 수용 금지**: Copilot 생성 코드는 반드시 이해 → 검증 → 테스트 3단계 거치기
4. **Hallucination 경계**: 존재하지 않는 API, 잘못된 설정값을 항상 의심하기
5. **버전 확인 습관화**: 사용 기술 스택의 최신 버전 기준으로 deprecated 여부 확인
