package com.example.common.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 재고 업데이트 이벤트 Product Service에서 발행하여 Order Service에서 소비 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdatedEvent {
  private Long productId;
  private String productName;
  private Integer previousStock;
  private Integer currentStock;
  private Integer changeAmount;
  private String reason; // ORDER, RESTOCK, ADJUSTMENT
  private LocalDateTime updatedAt;
}
