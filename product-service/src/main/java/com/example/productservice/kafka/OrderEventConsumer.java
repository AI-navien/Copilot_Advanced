package com.example.productservice.kafka;

import com.example.common.event.OrderCreatedEvent;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

  private final ProductService productService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "order-events", groupId = "product-service")
  public void handleOrderCreatedEvent(String message) {
    try {
      OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
      log.info("Received order created event: {}", event);

      // 재고 차감 처리
      productService.decreaseStock(event.getProductId(), event.getQuantity(), "ORDER");

    } catch (Exception e) {
      log.error("Failed to process order created event: {}", message, e);
      // 실제로는 DLQ(Dead Letter Queue)로 전송하거나 재시도 로직 필요
    }
  }
}
