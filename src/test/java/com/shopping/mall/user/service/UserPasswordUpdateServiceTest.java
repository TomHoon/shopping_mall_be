package com.shopping.mall.user.service;

import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserPasswordUpdateRequestDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class UserPasswordUpdateServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User testUser;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test!@#")
                .name("test")
                .createdAt(now)
                .status(UserStatus.ACTIVE)
                .build();

    }

    @Test
    void updatePassword() {

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(testUser));

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "test!@#",
                "newPassword!@#",
                "newPassword!@#"
        );

        given(passwordEncoder.encode(requestDto.newPassword())).willReturn("encoded_password");
        given(passwordEncoder.matches(requestDto.currentPassword(), testUser.getPassword())).willReturn(true);
        given(passwordEncoder.matches(requestDto.newPassword(), testUser.getPassword())).willReturn(false);

        userService.updatePassword("test@test.com", requestDto);

        assertThat(testUser.getPassword()).isEqualTo("encoded_password");

        then(userRepository).should(times(1)).findByEmail("test@test.com");

    }

    @Test
    void updatePassword_notUser() {

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.empty());

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "test!@#",
                "newPassword!@#",
                "newPassword!@#"
        );


        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> userService.updatePassword("test@test.com", requestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());

    }

    @Test
    void updatePassword_invalidPasswordConfirm() {

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(testUser));

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "test!@#",
                "newPassword!@#",
                "falsePassword"
        );

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> userService.updatePassword("test@test.com", requestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_PASSWORD.getMessage());

    }

    @Test
    void updatePassword_invalidCurrentPassword() {

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(testUser));

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "falsePassword",
                "newPassword!@#",
                "newPassword!@#"
        );

        given(passwordEncoder.matches(requestDto.currentPassword(), testUser.getPassword())).willReturn(false);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> userService.updatePassword("test@test.com", requestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_PASSWORD.getMessage());

    }

    @Test
    void updatePassword_invalidNewPassword() {

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(testUser));

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "test!@#",
                "test!@#",
                "test!@#"
        );

        given(passwordEncoder.matches(requestDto.currentPassword(), testUser.getPassword())).willReturn(true);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> userService.updatePassword("test@test.com", requestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SAME_PASSWORD_NOT_ALLOWED.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.SAME_PASSWORD_NOT_ALLOWED.getMessage());

    }
}
