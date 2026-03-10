package com.shopping.mall.user.service;

import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class UserProfileReadServiceTest {
    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

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
    void getUserProfile() {

        UserProfileResponseDto dto = UserProfileResponseDto.from(testUser);

        given(userMapper.findByUserEmail("test@test.com"))
                .willReturn(Optional.of(dto));

        UserProfileResponseDto response = userService.getUserProfile("test@test.com");

        assertThat(response).isEqualTo(dto);

        then(userMapper).should(times(1)).findByUserEmail("test@test.com");
    }

    @Test
    void getUserProfile_notUser() {

        given(userMapper.findByUserEmail("test@test.com"))
                .willReturn(Optional.empty());

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> userService.getUserProfile("test@test.com"));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
