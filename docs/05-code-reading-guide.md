# 5. AI 코드 읽기 가이드 (Code Reading)

## 학습 목표
- AI가 생성한 코드를 빠르게 이해하는 방법 습득
- Copilot을 활용한 코드 분석 기법 학습

---

## 교육 내용

### 5.1 AI 코드 이해의 중요성

AI가 생성한 코드를 **맹목적으로 사용하면 안 됩니다**.
반드시 **이해하고 검증**한 후 사용해야 합니다.

### 5.2 코드 이해 체크리스트

#### 1️⃣ 함수/메서드 분석
- **입력(파라미터)**: 무엇을 받는가?
- **출력(반환값)**: 무엇을 돌려주는가?
- **부수효과(Side Effect)**: DB 저장, 이벤트 발행 등

#### 2️⃣ 호출 방식 분석
- **누가 호출하는가?**: Controller → Service → Repository
- **언제 호출되는가?**: HTTP 요청 시? 이벤트 수신 시?
- **동기/비동기**: 블로킹? 논블로킹?

#### 3️⃣ 타겟 분석
- **어떤 데이터를 다루는가?**: Entity, DTO
- **어떤 외부 시스템과 통신하는가?**: DB, Kafka, 다른 서비스

### 5.3 Copilot으로 코드 분석하기

#### 메서드 설명 요청

```
이 메서드가 무엇을 하는지 설명해주세요:
- 입력값
- 반환값
- 주요 로직
- 호출되는 메서드
```

#### 호출 흐름 요청

```
createOrder 메서드의 호출 흐름을 설명해주세요.
1. 어디서 호출되는지
2. 내부에서 어떤 메서드를 호출하는지
3. 최종적으로 어떤 결과가 발생하는지
```

### 5.4 실전 예시: OrderService 분석

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    // 1. 주문 엔티티 생성
    Order order = Order.builder()
            .userId(request.getUserId())
            .productId(request.getProductId())
            .quantity(request.getQuantity())
            .totalPrice(request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity())))
            .status(OrderStatus.PENDING)
            .build();
    
    // 2. DB 저장
    Order savedOrder = orderRepository.save(order);
    
    // 3. Kafka 이벤트 발행
    OrderCreatedEvent event = OrderCreatedEvent.builder()
            .orderId(savedOrder.getId())
            .userId(savedOrder.getUserId())
            .productId(savedOrder.getProductId())
            .quantity(savedOrder.getQuantity())
            .createdAt(LocalDateTime.now())
            .build();
    orderEventProducer.sendOrderCreatedEvent(event);
    
    // 4. 응답 DTO 변환
    return toResponse(savedOrder);
}
```

#### 분석 결과

| 항목 | 내용 |
|------|------|
| **입력** | CreateOrderRequest (userId, productId, quantity, unitPrice) |
| **출력** | OrderResponse (주문 정보 DTO) |
| **주요 로직** | 1. Order 엔티티 생성 → 2. DB 저장 → 3. Kafka 이벤트 발행 → 4. DTO 변환 |
| **부수효과** | DB INSERT, Kafka 메시지 발행 |
| **트랜잭션** | @Transactional 적용 (DB 저장까지만 트랜잭션) |
| **주의사항** | Kafka 발행 실패 시 DB 롤백 안 됨 (비동기 처리 필요) |

### 5.5 MSA 특화 분석 포인트

#### Kafka 이벤트 흐름 추적

```
OrderCreatedEvent가 발행되면:
1. 어떤 서비스가 수신하는가? → ProductService
2. 수신 후 어떤 처리를 하는가? → 재고 차감
3. 실패하면 어떻게 되는가? → 현재 로깅만 (TODO: DLQ 필요)
```

#### 서비스 간 의존성 파악

```
Order Service가 의존하는 서비스:
- User Service: 사용자 존재 여부 확인 (현재 미구현)
- Product Service: 재고 확인 (Kafka로 비동기 처리)

Product Service가 의존하는 서비스:
- Order Service: 주문 이벤트 수신
```

---

## 실습

### 실습 1: 코드 분석
`ProductService.decreaseStock()` 메서드를 분석해보세요:
- 입력/출력
- 주요 로직
- 부수효과
- 예외 상황

### 실습 2: Copilot 활용 분석
Copilot Chat에 다음 프롬프트를 입력하세요:

```
UserEventProducer.sendUserCreatedEvent 메서드를 분석해주세요.
1. 어떤 데이터를 Kafka로 보내는지
2. 실패할 수 있는 경우
3. 개선할 점
```

---

## 핵심 포인트
1. **맹목적 신뢰 금지**: 반드시 이해 후 사용
2. **체계적 분석**: 입력 → 로직 → 출력 → 부수효과
3. **Copilot 활용**: 코드 설명 요청으로 이해 가속화
4. **MSA 특화**: 이벤트 흐름, 서비스 간 의존성 파악

