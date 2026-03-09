package com.shopping.mall.user.controller;

import com.shopping.mall.auth.CustomUserDetails;
import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        User testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .createdAt(now)
                .status(UserStatus.ACTIVE)
                .build();

        customUserDetails = new CustomUserDetails(testUser);
    }

    @Test
    void getUserProfile() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        UserProfileResponseDto responseDto = UserProfileResponseDto.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .createdAt(now)
                .status(UserStatus.ACTIVE)
                .build();

        given(userService.getUserProfile(anyString())).willReturn(responseDto);

        mockMvc.perform(get("/api/user/profile")
                        .with(user(customUserDetails)))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk());
    }
}