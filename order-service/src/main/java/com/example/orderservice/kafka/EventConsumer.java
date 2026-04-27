package com.example.orderservice.kafka;

import com.example.common.event.StockUpdatedEvent;
import com.example.common.event.UserCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "user-events", groupId = "order-service")
  public void handleUserCreatedEvent(String message) {
    try {
      UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
      log.info("Received user created event: {}", event);

      // 사용자 정보 캐싱 또는 로컬 저장 로직
      // 실제로는 사용자 정보를 로컬 DB에 저장하거나 캐시할 수 있음

    } catch (Exception e) {
      log.error("Failed to process user created event: {}", message, e);
    }
  }

  @KafkaListener(topics = "stock-events", groupId = "order-service")
  public void handleStockUpdatedEvent(String message) {
    try {
      StockUpdatedEvent event = objectMapper.readValue(message, StockUpdatedEvent.class);
      log.info("Received stock updated event: {}", event);

      // 재고 변경에 따른 주문 상태 업데이트 로직
      // 예: 재고 차감이 완료되면 주문 상태를 CONFIRMED로 변경

    } catch (Exception e) {
      log.error("Failed to process stock updated event: {}", message, e);
    }
  }
}
