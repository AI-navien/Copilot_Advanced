package com.example.orderservice.kafka;

import com.example.common.event.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

  private static final String TOPIC = "order-events";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void sendOrderCreatedEvent(OrderCreatedEvent event) {
    try {
      String message = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), message);
      log.info("Order created event sent: {}", event);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize OrderCreatedEvent", e);
      throw new RuntimeException("Failed to send order created event", e);
    }
  }
}
