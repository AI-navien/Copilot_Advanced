package com.example.common.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 주문 생성 이벤트 Order Service에서 발행하여 Product Service에서 소비 (재고 차감) */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
  private Long orderId;
  private Long userId;
  private Long productId;
  private Integer quantity;
  private LocalDateTime createdAt;
}
