package com.shopping.mall.user.service;

import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @Test
    void getUserProfile() {
        LocalDateTime now = LocalDateTime.now();

        User testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .createdAt(now)
                .status(UserStatus.ACTIVE)
                .build();

        UserProfileResponseDto dto = UserProfileResponseDto.from(testUser);

        given(userMapper.findByUserEmail("test@test.com"))
                .willReturn(Optional.of(dto));

        UserProfileResponseDto response = userService.getUserProfile("test@test.com");

        assertThat(response).isEqualTo(dto);

        then(userMapper).should(times(1)).findByUserEmail("test@test.com");
    }
}
