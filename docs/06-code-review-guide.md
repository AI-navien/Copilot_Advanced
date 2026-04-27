# 6. 코드 검증(리뷰) 가이드 (Code Review)

## 학습 목표
- AI 생성 코드의 신뢰성 검증 방법 습득
- 코드 리뷰 시 중점적으로 봐야 할 부분 학습
- 검증 시간 단축을 위한 AI 활용법 습득

---

## 교육 내용

### 6.1 왜 코드 검증이 필요한가?

AI가 생성한 코드는 다음 문제가 있을 수 있습니다:
- **논리적 오류**: 요구사항과 다른 동작
- **보안 취약점**: SQL Injection, XSS 등
- **성능 문제**: N+1 쿼리, 메모리 누수
- **표준 위반**: 코딩 컨벤션, 아키텍처 규칙

### 6.2 코드 검증 체크리스트

#### 1️⃣ 정확성 검증
- [ ] 요구사항대로 동작하는가?
- [ ] 엣지 케이스 처리가 되어 있는가?
- [ ] 예외 처리가 적절한가?

#### 2️⃣ 보안 검증
- [ ] 입력값 검증이 되어 있는가?
- [ ] 민감 정보가 노출되지 않는가?
- [ ] 인증/인가가 적절한가?

#### 3️⃣ 성능 검증
- [ ] 불필요한 DB 호출이 없는가?
- [ ] N+1 쿼리 문제가 없는가?
- [ ] 메모리 효율적인가?

#### 4️⃣ 유지보수성 검증
- [ ] 코드가 읽기 쉬운가?
- [ ] 중복 코드가 없는가?
- [ ] 테스트 가능한 구조인가?

### 6.3 AI 활용 코드 검증

#### 보안 취약점 검사 프롬프트

```
다음 코드의 보안 취약점을 검사해주세요:
- SQL Injection 가능성
- XSS 가능성
- 민감 정보 노출
- 인증/인가 문제

[코드 붙여넣기]
```

#### 성능 문제 검사 프롬프트

```
다음 코드의 성능 문제를 분석해주세요:
- N+1 쿼리 문제
- 불필요한 DB 호출
- 메모리 비효율
- 병목 지점

[코드 붙여넣기]
```

#### 코드 품질 검사 프롬프트

```
다음 코드의 품질을 검토하고 개선점을 알려주세요:
- 가독성
- 중복 코드
- 단일 책임 원칙 위반
- 테스트 용이성

[코드 붙여넣기]
```

### 6.4 실전 예시: 문제 있는 코드 찾기

다음 코드에서 문제점을 찾아보세요:

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    // 문제 1: 사용자 존재 여부 확인 없음
    // 문제 2: 상품 존재 여부 확인 없음
    // 문제 3: 재고 확인 없음 (비동기 처리로 인한 정합성 문제)
    
    Order order = Order.builder()
            .userId(request.getUserId())
            .productId(request.getProductId())
            .quantity(request.getQuantity())
            .totalPrice(request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity())))
            .status(OrderStatus.PENDING)
            .build();
    
    Order savedOrder = orderRepository.save(order);
    
    // 문제 4: Kafka 발행 실패 시 처리 없음
    // 문제 5: 트랜잭션 외부에서 Kafka 발행 필요
    orderEventProducer.sendOrderCreatedEvent(/* ... */);
    
    return toResponse(savedOrder);
}
```

#### 발견된 문제점

| 문제 | 심각도 | 개선 방안 |
|------|--------|-----------|
| 사용자 존재 확인 없음 | 중 | User Service 호출 또는 로컬 캐시 확인 |
| 상품 존재 확인 없음 | 중 | Product Service 호출 필요 |
| 재고 동기 확인 없음 | 상 | SAGA 패턴 또는 동기 호출 고려 |
| Kafka 실패 처리 없음 | 상 | Transactional Outbox 패턴 적용 |
| 트랜잭션 범위 문제 | 중 | @TransactionalEventListener 사용 |

### 6.5 Copilot Instruction 활용 코드 검증

프로젝트의 `.github/copilot-instructions.md`에 검증 규칙을 추가하면
AI가 처음부터 더 나은 코드를 생성합니다:

```markdown
## 코드 검증 규칙
- 모든 외부 입력은 @Valid로 검증
- 엔티티 조회 실패 시 IllegalArgumentException
- Kafka 발행은 트랜잭션 완료 후 처리
- N+1 방지를 위해 @EntityGraph 또는 fetch join 사용
```

---

## 실습

### 실습 1: 수동 코드 리뷰
`UserService.createUser()` 메서드를 열고 다음을 확인하세요:
- 입력값 검증
- 중복 체크
- 예외 처리
- 트랜잭션 범위

### 실습 2: AI 활용 코드 리뷰
Copilot Chat에 다음 프롬프트를 입력하세요:

```
OrderService 클래스의 전체 코드를 리뷰하고
문제점과 개선 방안을 알려주세요.
특히 MSA 환경에서의 데이터 정합성 문제를 중점적으로 봐주세요.
```

---

## 핵심 포인트
1. **무조건 검증**: AI 코드도 반드시 리뷰 필요
2. **체크리스트 활용**: 정확성, 보안, 성능, 유지보수성
3. **AI로 검증 가속화**: 프롬프트로 문제점 빠르게 파악
4. **Instruction에 규칙 추가**: 처음부터 좋은 코드 생성 유도

