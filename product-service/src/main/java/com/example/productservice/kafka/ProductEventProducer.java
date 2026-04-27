package com.example.productservice.kafka;

import com.example.common.event.StockUpdatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

  private static final String TOPIC = "stock-events";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void sendStockUpdatedEvent(StockUpdatedEvent event) {
    try {
      String message = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(TOPIC, String.valueOf(event.getProductId()), message);
      log.info("Stock updated event sent: {}", event);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize StockUpdatedEvent", e);
      throw new RuntimeException("Failed to send stock updated event", e);
    }
  }
}
