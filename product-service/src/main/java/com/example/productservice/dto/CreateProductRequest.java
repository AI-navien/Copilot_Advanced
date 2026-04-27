package com.example.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class CreateProductRequest {

  @NotBlank(message = "상품명은 필수입니다")
  private String name;

  private String description;

  @NotNull(message = "가격은 필수입니다")
  @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
  private BigDecimal price;

  @NotNull(message = "재고는 필수입니다")
  @Min(value = 0, message = "재고는 0 이상이어야 합니다")
  private Integer stock;

  private String category;
}
