# 8. 리팩토링 가이드 (Refactoring Guide)

## 학습 목표
- 코드 검증/테스트에서 발견된 문제 해결 방법 습득
- Copilot을 활용한 리팩토링 기법 학습
- MSA 환경에서의 리팩토링 주의사항 이해

---

## 교육 내용

### 8.1 리팩토링 시점

6단계(코드 검증), 7단계(테스트)에서 **문제가 발견되었을 때** 리팩토링을 진행합니다.

| 발견 단계 | 문제 유형 | 리팩토링 예시 |
|-----------|-----------|---------------|
| 코드 검증 | 가독성 문제 | 메서드 추출, 변수명 개선 |
| 코드 검증 | 중복 코드 | 공통 메서드 추출 |
| 코드 검증 | 성능 문제 | 쿼리 최적화, 캐싱 |
| 테스트 | 테스트 어려움 | 의존성 주입 개선 |
| 테스트 | 버그 발견 | 로직 수정 |

### 8.2 Copilot 활용 리팩토링

#### 가독성 개선 프롬프트

```
다음 메서드의 가독성을 개선해주세요:
- 긴 메서드를 작은 메서드로 분리
- 의미 있는 변수명 사용
- 주석 추가

[코드 붙여넣기]
```

#### 중복 코드 제거 프롬프트

```
다음 두 메서드의 중복 코드를 제거하고
공통 메서드로 추출해주세요:

[메서드1 코드]

[메서드2 코드]
```

#### 성능 개선 프롬프트

```
다음 코드의 성능을 개선해주세요:
- N+1 쿼리 문제 해결
- 불필요한 DB 호출 제거
- 적절한 인덱스 제안

[코드 붙여넣기]
```

### 8.3 리팩토링 실전 예시

#### Before: 긴 메서드

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    // 30줄 이상의 복잡한 로직...
    Order order = Order.builder()
            .userId(request.getUserId())
            .productId(request.getProductId())
            .quantity(request.getQuantity())
            .totalPrice(request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity())))
            .status(OrderStatus.PENDING)
            .build();
    
    Order savedOrder = orderRepository.save(order);
    
    OrderCreatedEvent event = OrderCreatedEvent.builder()
            .orderId(savedOrder.getId())
            .userId(savedOrder.getUserId())
            .productId(savedOrder.getProductId())
            .quantity(savedOrder.getQuantity())
            .createdAt(LocalDateTime.now())
            .build();
    orderEventProducer.sendOrderCreatedEvent(event);
    
    return toResponse(savedOrder);
}
```

#### After: 메서드 분리

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    Order order = buildOrder(request);
    Order savedOrder = orderRepository.save(order);
    publishOrderCreatedEvent(savedOrder);
    return toResponse(savedOrder);
}

private Order buildOrder(CreateOrderRequest request) {
    BigDecimal totalPrice = calculateTotalPrice(request);
    return Order.builder()
            .userId(request.getUserId())
            .productId(request.getProductId())
            .quantity(request.getQuantity())
            .totalPrice(totalPrice)
            .status(OrderStatus.PENDING)
            .build();
}

private BigDecimal calculateTotalPrice(CreateOrderRequest request) {
    return request.getUnitPrice()
            .multiply(BigDecimal.valueOf(request.getQuantity()));
}

private void publishOrderCreatedEvent(Order order) {
    OrderCreatedEvent event = OrderCreatedEvent.builder()
            .orderId(order.getId())
            .userId(order.getUserId())
            .productId(order.getProductId())
            .quantity(order.getQuantity())
            .createdAt(LocalDateTime.now())
            .build();
    orderEventProducer.sendOrderCreatedEvent(event);
}
```

### 8.4 MSA 리팩토링 주의사항

#### 1️⃣ 영향도 분석 필수

```
리팩토링 전 확인사항:
- 이 메서드를 호출하는 곳은 어디인가?
- API 스펙이 변경되는가?
- Kafka 이벤트 구조가 변경되는가?
- 다른 서비스에 영향을 주는가?
```

#### 2️⃣ 하위 호환성 유지

```java
// 기존 이벤트 구조
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    // ...
}

// 필드 추가 시 (하위 호환)
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private String orderNumber;  // 새 필드 추가 (기존 Consumer 영향 없음)
    // ...
}

// 필드 삭제/변경 시 (하위 호환 X - 주의!)
// → 버전 관리 또는 새 토픽 사용 고려
```

#### 3️⃣ 배포 전략

```
MSA 리팩토링 배포 순서:
1. Consumer 먼저 배포 (새 필드 처리 가능하도록)
2. Producer 배포 (새 필드 포함하여 발행)
3. 롤백 계획 준비
```

### 8.5 Copilot으로 영향도 분석

```
createOrder 메서드를 리팩토링하려고 합니다.
다음 변경사항의 영향도를 분석해주세요:

변경 내용:
- OrderCreatedEvent에 orderNumber 필드 추가
- 반환 타입을 OrderResponse에서 OrderDetailResponse로 변경

확인이 필요한 사항:
1. 이 변경으로 영향받는 다른 서비스
2. API 클라이언트 영향
3. 하위 호환성 문제
4. 테스트 수정 필요 여부
```

---

## 실습

### 실습 1: 메서드 분리 리팩토링
`ProductService.decreaseStock()` 메서드를 분석하고:
1. 책임별로 메서드 분리
2. 가독성 개선
3. 테스트 용이성 향상

### 실습 2: Copilot 활용 리팩토링
Copilot Chat에 다음 프롬프트를 입력하세요:

```
UserController 클래스를 리팩토링해주세요.
1. 예외 처리를 @ControllerAdvice로 분리
2. 입력 검증 로직 개선
3. 응답 형식 일관성 확보
```

---

## 핵심 포인트
1. **문제 발견 시 리팩토링**: 6, 7단계 결과 기반
2. **작은 단위로 진행**: 한 번에 하나씩 개선
3. **테스트 먼저**: 리팩토링 전 테스트 확보
4. **MSA 영향도**: 다른 서비스 영향 필수 확인
5. **하위 호환성**: API/이벤트 변경 시 주의

