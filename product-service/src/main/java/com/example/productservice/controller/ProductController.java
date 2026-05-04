package com.example.productservice.controller;

import com.example.common.dto.ApiResponse;
import com.example.productservice.dto.CreateProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
      @Valid @RequestBody CreateProductRequest request) {
    ProductResponse product = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("상품이 생성되었습니다", product));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
    ProductResponse product = productService.getProductById(id);
    return ResponseEntity.ok(ApiResponse.success(product));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
    List<ProductResponse> products = productService.getAllProducts();
    return ResponseEntity.ok(ApiResponse.success(products));
  }

  @GetMapping("/category/{categoryName}")
  public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
      @PathVariable String categoryName) {
    List<ProductResponse> products = productService.findByCategory(categoryName);
    return ResponseEntity.ok(ApiResponse.success(products));
  }

  @PatchMapping("/{id}/stock/increase")
  public ResponseEntity<ApiResponse<Void>> increaseStock(
      @PathVariable Long id, @RequestParam Integer quantity) {
    productService.increaseStock(id, quantity, "RESTOCK");
    return ResponseEntity.ok(ApiResponse.success("재고가 증가되었습니다", null));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok(ApiResponse.success("상품이 삭제되었습니다", null));
  }
}
