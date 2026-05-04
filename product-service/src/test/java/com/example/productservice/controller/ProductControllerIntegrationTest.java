package com.example.productservice.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.productservice.dto.ProductResponse;
import com.example.productservice.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController 통합 테스트")
class ProductControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductService productService;

  @Test
  @DisplayName("카테고리별 조회 성공 시 해당 카테고리 상품 목록을 반환해야 한다")
  void should_returnProducts_when_categorySearchSucceeds() throws Exception {
    // given
    String categoryName = "전자제품";
    List<ProductResponse> products =
        List.of(
            ProductResponse.builder()
                .id(1L)
                .name("노트북")
                .description("고성능 노트북")
                .price(BigDecimal.valueOf(1500000))
                .stock(10)
                .category(categoryName)
                .createdAt(LocalDateTime.now())
                .build(),
            ProductResponse.builder()
                .id(2L)
                .name("마우스")
                .description("무선 마우스")
                .price(BigDecimal.valueOf(50000))
                .stock(50)
                .category(categoryName)
                .createdAt(LocalDateTime.now())
                .build());

    when(productService.findByCategory(categoryName)).thenReturn(products);

    // when & then
    mockMvc
        .perform(get("/api/products/category/{categoryName}", categoryName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[*].category", everyItem(is(categoryName))))
        .andExpect(jsonPath("$.data[*].name", containsInAnyOrder("노트북", "마우스")));

    verify(productService).findByCategory(categoryName);
  }

  @Test
  @DisplayName("존재하지 않는 카테고리 조회 시 빈 목록을 반환해야 한다")
  void should_returnEmptyList_when_categoryNotExists() throws Exception {
    // given
    String categoryName = "없는카테고리";
    when(productService.findByCategory(categoryName)).thenReturn(List.of());

    // when & then
    mockMvc
        .perform(get("/api/products/category/{categoryName}", categoryName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(0)));

    verify(productService).findByCategory(categoryName);
  }
}


  @Autowired private MockMvc mockMvc;

  @Autowired private ProductRepository productRepository;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  @Test
  @DisplayName("카테고리별 조회 성공 시 해당 카테고리 상품 목록을 반환해야 한다")
  void should_returnProducts_when_categorySearchSucceeds() throws Exception {
    // given
    productRepository.save(
        Product.builder()
            .name("노트북")
            .description("고성능 노트북")
            .price(BigDecimal.valueOf(1500000))
            .stock(10)
            .category("전자제품")
            .build());
    productRepository.save(
        Product.builder()
            .name("마우스")
            .description("무선 마우스")
            .price(BigDecimal.valueOf(50000))
            .stock(50)
            .category("전자제품")
            .build());
    productRepository.save(
        Product.builder()
            .name("티셔츠")
            .description("면 티셔츠")
            .price(BigDecimal.valueOf(20000))
            .stock(100)
            .category("의류")
            .build());

    // when & then
    mockMvc
        .perform(get("/api/products/category/{categoryName}", "전자제품"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.data[*].category", everyItem(is("전자제품"))))
        .andExpect(jsonPath("$.data[*].name", containsInAnyOrder("노트북", "마우스")));
  }

  @Test
  @DisplayName("존재하지 않는 카테고리 조회 시 빈 목록을 반환해야 한다")
  void should_returnEmptyList_when_categoryNotExists() throws Exception {
    // given
    productRepository.save(
        Product.builder()
            .name("노트북")
            .description("고성능 노트북")
            .price(BigDecimal.valueOf(1500000))
            .stock(10)
            .category("전자제품")
            .build());

    // when & then
    mockMvc
        .perform(get("/api/products/category/{categoryName}", "없는카테고리"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(0)));
  }
}
