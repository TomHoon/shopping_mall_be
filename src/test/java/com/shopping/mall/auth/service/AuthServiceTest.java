package com.shopping.mall.auth.service;

import com.shopping.mall.auth.dto.LoginRequestDTO;
import com.shopping.mall.auth.dto.LoginResponseDTO;
import com.shopping.mall.common.JwtUtil;
import com.shopping.mall.user.entity.Role;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
    UserMapper userMapper;

	@Mock
    JwtUtil jwtUtil;

	@Mock
    PasswordEncoder passwordEncoder;

	@InjectMocks
    AuthService authService;


    @Test
    void loginUser() {
        User testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("test!1@2")
                .name("test")
                .status(UserStatus.ACTIVE)
                .build();

        Role role = Role.builder()
                .id(1L)
                .roleName("USER")
                .build();

        given(userMapper.findByEmail("test@test.com"))
                .willReturn(testUser);

        given(userMapper.findUserRole(testUser.getId())).willReturn(role.getRoleName());

        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        given(jwtUtil.createToken(anyString(), anyString()))
                .willReturn("fake-access-token");

        given(jwtUtil.createRefreshToken(anyString()))
                .willReturn("fake-refresh-token");

        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "test!1@2");

        LoginResponseDTO response = authService.login(request);

        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
    }
}