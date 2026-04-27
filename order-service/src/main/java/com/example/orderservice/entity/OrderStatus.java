package com.example.orderservice.entity;

public enum OrderStatus {
  PENDING, // 주문 대기
  CONFIRMED, // 주문 확정
  PROCESSING, // 처리 중
  SHIPPED, // 배송 중
  DELIVERED, // 배송 완료
  CANCELLED // 취소됨
}
