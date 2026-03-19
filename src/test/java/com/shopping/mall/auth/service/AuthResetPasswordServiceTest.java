package com.shopping.mall.auth.service;

import com.shopping.mall.auth.dto.ResetPasswordRequestDTO;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthResetPasswordServiceTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    User testUser;

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
    void resetPassword() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "test@test.com",
                "newpassword",
                "newpassword"
        );

        given(redisTemplate.hasKey("verify:" + requestDTO.email())).willReturn(true);
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(testUser));
        given(passwordEncoder.encode(requestDTO.newPassword())).willReturn("encoded_password");

        authService.resetPassword(requestDTO);

        assertThat(testUser.getPassword()).isEqualTo("encoded_password");
        then(userRepository).should(times(1)).findByEmail("test@test.com");
        then(redisTemplate).should(times(1)).delete("verify:" + requestDTO.email());
    }

    @Test
    void resetPassword_invalidPassword() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "test@test.com",
                "newpassword",
                "notpassword"
        );

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> authService.resetPassword(requestDTO));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    void resetPassword_isNotVerify() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "test@test.com",
                "newpassword",
                "newpassword"
        );

        given(redisTemplate.hasKey("verify:" + requestDTO.email())).willReturn(false);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> authService.resetPassword(requestDTO));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_EMAIL.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.UNAUTHORIZED_EMAIL.getMessage());
    }

    @Test
    void resetPassword_userNotFound() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "notUser@test.com",
                "newpassword",
                "newpassword"
        );

        given(redisTemplate.hasKey("verify:" + requestDTO.email())).willReturn(true);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> authService.resetPassword(requestDTO));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
