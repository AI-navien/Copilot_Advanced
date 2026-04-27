package com.example.orderservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.kafka.OrderEventProducer;
import com.example.orderservice.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private OrderEventProducer orderEventProducer;

  @InjectMocks private OrderService orderService;

  @Test
  @DisplayName("정상적으로 주문이 생성되어야 한다")
  void should_CreateOrder_when_ValidRequest() {
    // given
    CreateOrderRequest request =
        CreateOrderRequest.builder()
            .userId(1L)
            .productId(1L)
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(10000))
            .build();

    Order savedOrder =
        Order.builder()
            .id(1L)
            .userId(1L)
            .productId(1L)
            .quantity(2)
            .totalPrice(BigDecimal.valueOf(20000))
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    // when
    OrderResponse response = orderService.createOrder(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getTotalPrice()).isEqualTo(BigDecimal.valueOf(20000));
    assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);

    verify(orderRepository).save(any(Order.class));
    verify(orderEventProducer).sendOrderCreatedEvent(any());
  }

  @Test
  @DisplayName("ID로 주문을 조회할 수 있어야 한다")
  void should_GetOrderById_when_OrderExists() {
    // given
    Order order =
        Order.builder()
            .id(1L)
            .userId(1L)
            .productId(1L)
            .quantity(2)
            .totalPrice(BigDecimal.valueOf(20000))
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    // when
    OrderResponse response = orderService.getOrderById(1L);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("존재하지 않는 주문 조회 시 예외가 발생해야 한다")
  void should_ThrowException_when_OrderNotFound() {
    // given
    when(orderRepository.findById(999L)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> orderService.getOrderById(999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("주문을 찾을 수 없습니다");
  }

  @Test
  @DisplayName("사용자 ID로 주문 목록을 조회할 수 있어야 한다")
  void should_GetOrdersByUserId_when_OrdersExist() {
    // given
    List<Order> orders =
        Arrays.asList(
            Order.builder()
                .id(1L)
                .userId(1L)
                .productId(1L)
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(10000))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build(),
            Order.builder()
                .id(2L)
                .userId(1L)
                .productId(2L)
                .quantity(2)
                .totalPrice(BigDecimal.valueOf(20000))
                .status(OrderStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build());

    when(orderRepository.findByUserId(1L)).thenReturn(orders);

    // when
    List<OrderResponse> responses = orderService.getOrdersByUserId(1L);

    // then
    assertThat(responses).hasSize(2);
  }

  @Test
  @DisplayName("주문 상태를 변경할 수 있어야 한다")
  void should_UpdateOrderStatus_when_OrderExists() {
    // given
    Order order =
        Order.builder()
            .id(1L)
            .userId(1L)
            .productId(1L)
            .quantity(2)
            .totalPrice(BigDecimal.valueOf(20000))
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // when
    OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

    // then
    assertThat(response.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    verify(orderRepository).save(order);
  }

  @Test
  @DisplayName("PENDING 상태의 주문을 취소할 수 있어야 한다")
  void should_CancelOrder_when_StatusIsPending() {
    // given
    Order order =
        Order.builder()
            .id(1L)
            .userId(1L)
            .productId(1L)
            .quantity(2)
            .totalPrice(BigDecimal.valueOf(20000))
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // when
    OrderResponse response = orderService.cancelOrder(1L);

    // then
    assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
  }

  @Test
  @DisplayName("배송 중인 주문은 취소할 수 없어야 한다")
  void should_ThrowException_when_CancelShippedOrder() {
    // given
    Order order =
        Order.builder()
            .id(1L)
            .userId(1L)
            .productId(1L)
            .quantity(2)
            .totalPrice(BigDecimal.valueOf(20000))
            .status(OrderStatus.SHIPPED)
            .createdAt(LocalDateTime.now())
            .build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    // when & then
    assertThatThrownBy(() -> orderService.cancelOrder(1L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("배송 중이거나 배송 완료된 주문은 취소할 수 없습니다");
  }
}
