package com.example.orderservice.service;

import com.example.common.event.OrderCreatedEvent;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.kafka.OrderEventProducer;
import com.example.orderservice.repository.OrderRepository;
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
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderEventProducer orderEventProducer;

  @Transactional
  public OrderResponse createOrder(CreateOrderRequest request) {
    // 주문 생성
    Order order =
        Order.builder()
            .userId(request.getUserId())
            .productId(request.getProductId())
            .quantity(request.getQuantity())
            .totalPrice(
                request
                    .getUnitPrice()
                    .multiply(java.math.BigDecimal.valueOf(request.getQuantity())))
            .status(OrderStatus.PENDING)
            .build();

    Order savedOrder = orderRepository.save(order);
    log.info("Order created: {}", savedOrder.getId());

    // Kafka 이벤트 발행 (Product Service에서 재고 차감 처리)
    OrderCreatedEvent event =
        OrderCreatedEvent.builder()
            .orderId(savedOrder.getId())
            .userId(savedOrder.getUserId())
            .productId(savedOrder.getProductId())
            .quantity(savedOrder.getQuantity())
            .createdAt(LocalDateTime.now())
            .build();
    orderEventProducer.sendOrderCreatedEvent(event);

    return toResponse(savedOrder);
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrderById(Long id) {
    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));
    return toResponse(order);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> getOrdersByUserId(Long userId) {
    return orderRepository.findByUserId(userId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> getAllOrders() {
    return orderRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  @Transactional
  public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));

    order.setStatus(status);
    Order updatedOrder = orderRepository.save(order);
    log.info("Order status updated: {} -> {}", id, status);

    return toResponse(updatedOrder);
  }

  @Transactional
  public OrderResponse cancelOrder(Long id) {
    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));

    if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
      throw new IllegalStateException("배송 중이거나 배송 완료된 주문은 취소할 수 없습니다");
    }

    order.setStatus(OrderStatus.CANCELLED);
    Order cancelledOrder = orderRepository.save(order);
    log.info("Order cancelled: {}", id);

    // TODO: 재고 복구 이벤트 발행 필요

    return toResponse(cancelledOrder);
  }

  private OrderResponse toResponse(Order order) {
    return OrderResponse.builder()
        .id(order.getId())
        .userId(order.getUserId())
        .productId(order.getProductId())
        .quantity(order.getQuantity())
        .totalPrice(order.getTotalPrice())
        .status(order.getStatus())
        .createdAt(order.getCreatedAt())
        .updatedAt(order.getUpdatedAt())
        .build();
  }
}
