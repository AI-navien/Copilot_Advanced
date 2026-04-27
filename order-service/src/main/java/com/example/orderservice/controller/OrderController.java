package com.example.orderservice.controller;

import com.example.common.dto.ApiResponse;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    OrderResponse order = orderService.createOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("주문이 생성되었습니다", order));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
    OrderResponse order = orderService.getOrderById(id);
    return ResponseEntity.ok(ApiResponse.success(order));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
    List<OrderResponse> orders = orderService.getAllOrders();
    return ResponseEntity.ok(ApiResponse.success(orders));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUserId(
      @PathVariable Long userId) {
    List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
    return ResponseEntity.ok(ApiResponse.success(orders));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
      @PathVariable Long id, @RequestParam OrderStatus status) {
    OrderResponse order = orderService.updateOrderStatus(id, status);
    return ResponseEntity.ok(ApiResponse.success("주문 상태가 변경되었습니다", order));
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
    OrderResponse order = orderService.cancelOrder(id);
    return ResponseEntity.ok(ApiResponse.success("주문이 취소되었습니다", order));
  }
}
