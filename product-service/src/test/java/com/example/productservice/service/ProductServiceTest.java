package com.example.productservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.productservice.dto.CreateProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.entity.Product;
import com.example.productservice.kafka.ProductEventProducer;
import com.example.productservice.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {

  @Mock private ProductRepository productRepository;

  @Mock private ProductEventProducer productEventProducer;

  @InjectMocks private ProductService productService;

  @Test
  @DisplayName("정상적으로 상품이 생성되어야 한다")
  void should_CreateProduct_when_ValidRequest() {
    // given
    CreateProductRequest request =
        CreateProductRequest.builder()
            .name("테스트 상품")
            .description("테스트 설명")
            .price(BigDecimal.valueOf(10000))
            .stock(100)
            .category("전자제품")
            .build();

    Product savedProduct =
        Product.builder()
            .id(1L)
            .name("테스트 상품")
            .description("테스트 설명")
            .price(BigDecimal.valueOf(10000))
            .stock(100)
            .category("전자제품")
            .createdAt(LocalDateTime.now())
            .build();

    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // when
    ProductResponse response = productService.createProduct(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getName()).isEqualTo("테스트 상품");
    assertThat(response.getStock()).isEqualTo(100);

    verify(productRepository).save(any(Product.class));
  }

  @Test
  @DisplayName("정상적으로 재고가 감소해야 한다")
  void should_DecreaseStock_when_SufficientStock() {
    // given
    Product product =
        Product.builder().id(1L).name("테스트 상품").stock(100).createdAt(LocalDateTime.now()).build();

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    // when
    productService.decreaseStock(1L, 10, "ORDER");

    // then
    assertThat(product.getStock()).isEqualTo(90);
    verify(productRepository).save(product);
    verify(productEventProducer).sendStockUpdatedEvent(any());
  }

  @Test
  @DisplayName("재고가 부족한 경우 예외가 발생해야 한다")
  void should_ThrowException_when_InsufficientStock() {
    // given
    Product product =
        Product.builder().id(1L).name("테스트 상품").stock(5).createdAt(LocalDateTime.now()).build();

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // when & then
    assertThatThrownBy(() -> productService.decreaseStock(1L, 10, "ORDER"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("재고가 부족합니다");

    verify(productRepository, never()).save(any());
    verify(productEventProducer, never()).sendStockUpdatedEvent(any());
  }

  @Test
  @DisplayName("존재하지 않는 상품의 재고 감소 시 예외가 발생해야 한다")
  void should_ThrowException_when_ProductNotFound() {
    // given
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> productService.decreaseStock(999L, 10, "ORDER"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("상품을 찾을 수 없습니다");
  }

  @Test
  @DisplayName("정상적으로 재고가 증가해야 한다")
  void should_IncreaseStock_when_ValidRequest() {
    // given
    Product product =
        Product.builder().id(1L).name("테스트 상품").stock(100).createdAt(LocalDateTime.now()).build();

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    // when
    productService.increaseStock(1L, 50, "RESTOCK");

    // then
    assertThat(product.getStock()).isEqualTo(150);
    verify(productRepository).save(product);
    verify(productEventProducer).sendStockUpdatedEvent(any());
  }

  @Test
  @DisplayName("카테고리에 상품이 있는 경우 상품 목록을 반환해야 한다")
  void should_returnProducts_when_categoryExists() {
    // given
    String categoryName = "전자제품";
    List<Product> products =
        List.of(
            Product.builder()
                .id(1L)
                .name("노트북")
                .description("고성능 노트북")
                .price(BigDecimal.valueOf(1500000))
                .stock(10)
                .category(categoryName)
                .createdAt(LocalDateTime.now())
                .build(),
            Product.builder()
                .id(2L)
                .name("마우스")
                .description("무선 마우스")
                .price(BigDecimal.valueOf(50000))
                .stock(50)
                .category(categoryName)
                .createdAt(LocalDateTime.now())
                .build());

    when(productRepository.findByCategory(categoryName)).thenReturn(products);

    // when
    List<ProductResponse> result = productService.findByCategory(categoryName);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("노트북");
    assertThat(result.get(0).getCategory()).isEqualTo(categoryName);
    assertThat(result.get(1).getName()).isEqualTo("마우스");
    assertThat(result.get(1).getCategory()).isEqualTo(categoryName);
    verify(productRepository).findByCategory(categoryName);
  }

  @Test
  @DisplayName("카테고리에 상품이 없는 경우 빈 목록을 반환해야 한다")
  void should_returnEmptyList_when_categoryNotExists() {
    // given
    String categoryName = "존재하지않는카테고리";
    when(productRepository.findByCategory(categoryName)).thenReturn(List.of());

    // when
    List<ProductResponse> result = productService.findByCategory(categoryName);

    // then
    assertThat(result).isEmpty();
    verify(productRepository).findByCategory(categoryName);
  }

  @Test
  @DisplayName("null 카테고리 전달 시 빈 목록을 반환해야 한다")
  void should_returnEmptyList_when_categoryIsNull() {
    // given
    when(productRepository.findByCategory(null)).thenReturn(List.of());

    // when
    List<ProductResponse> result = productService.findByCategory(null);

    // then
    assertThat(result).isEmpty();
    verify(productRepository).findByCategory(null);
  }
}
