package com.shopping.mall.user.service;

import com.shopping.mall.auth.CustomUserDetails;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.dto.UserProfileUpdateRequestDto;
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

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
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
    void updateUserProfile() {

        given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(testUser));

        UserProfileUpdateRequestDto requestDto = UserProfileUpdateRequestDto.builder()
                .name("NEW:TEST")
                .build();

        userService.updateUserProfile("test@test.com", requestDto);

        assertThat(testUser.getName()).isEqualTo("NEW:TEST");

        then(userRepository).should(times(1)).findByEmail("test@test.com");

    }
}
