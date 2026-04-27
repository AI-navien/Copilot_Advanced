package com.example.productservice.service;

import com.example.common.event.StockUpdatedEvent;
import com.example.productservice.dto.CreateProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.entity.Product;
import com.example.productservice.kafka.ProductEventProducer;
import com.example.productservice.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductEventProducer productEventProducer;

  @Transactional
  public ProductResponse createProduct(CreateProductRequest request) {
    Product product =
        Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .stock(request.getStock())
            .category(request.getCategory())
            .build();

    Product savedProduct = productRepository.save(product);
    log.info("Product created: {}", savedProduct.getId());

    return toResponse(savedProduct);
  }

  @Transactional(readOnly = true)
  public ProductResponse getProductById(Long id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));
    return toResponse(product);
  }

  @Transactional(readOnly = true)
  public List<ProductResponse> getAllProducts() {
    return productRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ProductResponse> getProductsByCategory(String category) {
    return productRepository.findByCategory(category).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public void decreaseStock(Long productId, Integer quantity, String reason) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

    if (product.getStock() < quantity) {
      throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + product.getStock());
    }

    Integer previousStock = product.getStock();
    product.setStock(previousStock - quantity);
    productRepository.save(product);

    log.info(
        "Stock decreased for product {}: {} -> {}", productId, previousStock, product.getStock());

    // 재고 변경 이벤트 발행
    StockUpdatedEvent event =
        StockUpdatedEvent.builder()
            .productId(productId)
            .productName(product.getName())
            .previousStock(previousStock)
            .currentStock(product.getStock())
            .changeAmount(-quantity)
            .reason(reason)
            .updatedAt(LocalDateTime.now())
            .build();
    productEventProducer.sendStockUpdatedEvent(event);
  }

  @Transactional
  public void increaseStock(Long productId, Integer quantity, String reason) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

    Integer previousStock = product.getStock();
    product.setStock(previousStock + quantity);
    productRepository.save(product);

    log.info(
        "Stock increased for product {}: {} -> {}", productId, previousStock, product.getStock());

    // 재고 변경 이벤트 발행
    StockUpdatedEvent event =
        StockUpdatedEvent.builder()
            .productId(productId)
            .productName(product.getName())
            .previousStock(previousStock)
            .currentStock(product.getStock())
            .changeAmount(quantity)
            .reason(reason)
            .updatedAt(LocalDateTime.now())
            .build();
    productEventProducer.sendStockUpdatedEvent(event);
  }

  @Transactional
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + id);
    }
    productRepository.deleteById(id);
    log.info("Product deleted: {}", id);
  }

  private ProductResponse toResponse(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .stock(product.getStock())
        .category(product.getCategory())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }
}
