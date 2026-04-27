package com.example.userservice.service;

import com.example.common.event.UserCreatedEvent;
import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.kafka.UserEventProducer;
import com.example.userservice.repository.UserRepository;
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
public class UserService {

  private final UserRepository userRepository;
  private final UserEventProducer userEventProducer;

  @Transactional
  public UserResponse createUser(CreateUserRequest request) {
    // 중복 체크
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + request.getUsername());
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.getEmail());
    }

    // 사용자 생성
    User user =
        User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(request.getPassword()) // 실제로는 암호화 필요
            .fullName(request.getFullName())
            .phoneNumber(request.getPhoneNumber())
            .build();

    User savedUser = userRepository.save(user);
    log.info("User created: {}", savedUser.getId());

    // Kafka 이벤트 발행
    UserCreatedEvent event =
        UserCreatedEvent.builder()
            .userId(savedUser.getId())
            .username(savedUser.getUsername())
            .email(savedUser.getEmail())
            .createdAt(LocalDateTime.now())
            .build();
    userEventProducer.sendUserCreatedEvent(event);

    return toResponse(savedUser);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
    return toResponse(user);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  @Transactional
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id);
    }
    userRepository.deleteById(id);
    log.info("User deleted: {}", id);
  }

  private UserResponse toResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .phoneNumber(user.getPhoneNumber())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}
