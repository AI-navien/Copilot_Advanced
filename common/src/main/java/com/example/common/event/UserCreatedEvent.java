package com.example.common.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 사용자 생성 이벤트 User Service에서 발행하여 Order Service에서 소비 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
  private Long userId;
  private String username;
  private String email;
  private LocalDateTime createdAt;
}
