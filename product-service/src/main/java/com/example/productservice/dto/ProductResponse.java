package com.example.productservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stock;
  private String category;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
