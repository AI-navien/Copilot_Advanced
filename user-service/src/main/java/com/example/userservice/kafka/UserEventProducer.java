package com.example.userservice.kafka;

import com.example.common.event.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

  private static final String TOPIC = "user-events";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void sendUserCreatedEvent(UserCreatedEvent event) {
    try {
      String message = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(TOPIC, String.valueOf(event.getUserId()), message);
      log.info("User created event sent: {}", event);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize UserCreatedEvent", e);
      throw new RuntimeException("Failed to send user created event", e);
    }
  }
}
