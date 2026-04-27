package com.example.userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.User;
import com.example.userservice.kafka.UserEventProducer;
import com.example.userservice.repository.UserRepository;
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
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserEventProducer userEventProducer;

  @InjectMocks private UserService userService;

  @Test
  @DisplayName("정상적으로 사용자가 생성되어야 한다")
  void should_CreateUser_when_ValidRequest() {
    // given
    CreateUserRequest request =
        CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .fullName("Test User")
            .build();

    User savedUser =
        User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .fullName("Test User")
            .createdAt(LocalDateTime.now())
            .build();

    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // when
    UserResponse response = userService.createUser(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getUsername()).isEqualTo("testuser");
    assertThat(response.getEmail()).isEqualTo("test@example.com");

    verify(userRepository).save(any(User.class));
    verify(userEventProducer).sendUserCreatedEvent(any());
  }

  @Test
  @DisplayName("이미 존재하는 username인 경우 예외가 발생해야 한다")
  void should_ThrowException_when_UsernameExists() {
    // given
    CreateUserRequest request =
        CreateUserRequest.builder()
            .username("existinguser")
            .email("test@example.com")
            .password("password123")
            .build();

    when(userRepository.existsByUsername("existinguser")).thenReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.createUser(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 존재하는 사용자명");

    verify(userRepository, never()).save(any());
    verify(userEventProducer, never()).sendUserCreatedEvent(any());
  }

  @Test
  @DisplayName("이미 존재하는 email인 경우 예외가 발생해야 한다")
  void should_ThrowException_when_EmailExists() {
    // given
    CreateUserRequest request =
        CreateUserRequest.builder()
            .username("newuser")
            .email("existing@example.com")
            .password("password123")
            .build();

    when(userRepository.existsByUsername("newuser")).thenReturn(false);
    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.createUser(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 존재하는 이메일");

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("ID로 사용자를 조회할 수 있어야 한다")
  void should_GetUserById_when_UserExists() {
    // given
    User user =
        User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .createdAt(LocalDateTime.now())
            .build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // when
    UserResponse response = userService.getUserById(1L);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getUsername()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생해야 한다")
  void should_ThrowException_when_UserNotFound() {
    // given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.getUserById(999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("사용자를 찾을 수 없습니다");
  }

  @Test
  @DisplayName("전체 사용자 목록을 조회할 수 있어야 한다")
  void should_GetAllUsers_when_UsersExist() {
    // given
    List<User> users =
        Arrays.asList(
            User.builder()
                .id(1L)
                .username("user1")
                .email("user1@test.com")
                .createdAt(LocalDateTime.now())
                .build(),
            User.builder()
                .id(2L)
                .username("user2")
                .email("user2@test.com")
                .createdAt(LocalDateTime.now())
                .build());

    when(userRepository.findAll()).thenReturn(users);

    // when
    List<UserResponse> responses = userService.getAllUsers();

    // then
    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).getUsername()).isEqualTo("user1");
    assertThat(responses.get(1).getUsername()).isEqualTo("user2");
  }

  @Test
  @DisplayName("사용자를 삭제할 수 있어야 한다")
  void should_DeleteUser_when_UserExists() {
    // given
    when(userRepository.existsById(1L)).thenReturn(true);

    // when
    userService.deleteUser(1L);

    // then
    verify(userRepository).deleteById(1L);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시 예외가 발생해야 한다")
  void should_ThrowException_when_DeleteNonExistentUser() {
    // given
    when(userRepository.existsById(999L)).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> userService.deleteUser(999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("사용자를 찾을 수 없습니다");

    verify(userRepository, never()).deleteById(any());
  }
}
