package com.shopping.mall.user.service;

import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserDeleteRequestDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.mapper.UserMapper;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserDeleteService {
    @Mock
    UserMapper userMapper;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;
    private User testUser;

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
    void deleteUser_success() {

        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(testUser));

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto("test!@#");

        String deletePassword = requestDto.deletePassword();
        String userPassword = testUser.getPassword();

        given(passwordEncoder.matches(deletePassword, userPassword)).willReturn(true);

        userService.deleteUser("test@test.com", requestDto);

        assertThat(testUser.getStatus()).isEqualTo(UserStatus.INACTIVE);

        then(userRepository).should(times(1)).findByEmail("test@test.com");

    }

    @Test
    void deleteUser_notUser() {
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.empty());

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto("test!@#");

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> userService.deleteUser("test@test.com", requestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void deleteUser_notPassword() {
        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(testUser));

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto("test!@#");

        String deletePassword = requestDto.deletePassword();
        String userPassword = testUser.getPassword();

        given(passwordEncoder.matches(deletePassword, userPassword)).willReturn(false);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> userService.deleteUser("test@test.com", requestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_PASSWORD.getMessage());
    }

}
