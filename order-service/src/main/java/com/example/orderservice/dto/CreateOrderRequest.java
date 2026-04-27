package com.example.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

  @NotNull(message = "사용자 ID는 필수입니다")
  private Long userId;

  @NotNull(message = "상품 ID는 필수입니다")
  private Long productId;

  @NotNull(message = "수량은 필수입니다")
  @Min(value = 1, message = "수량은 1 이상이어야 합니다")
  private Integer quantity;

  @NotNull(message = "단가는 필수입니다")
  private BigDecimal unitPrice;
}
