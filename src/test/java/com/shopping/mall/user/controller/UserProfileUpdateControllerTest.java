package com.shopping.mall.user.controller;

import com.shopping.mall.auth.CustomUserDetails;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserProfileUpdateRequestDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileUpdateControllerTest {

	@Autowired
	MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void updateUserProfile() throws Exception {

        UserProfileUpdateRequestDto requestDto = new UserProfileUpdateRequestDto("NEW:TEST");
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/api/user/profile")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk());

        then(userService).should(times(1)).updateUserProfile(eq(customUserDetails.getUser().getEmail()), any(UserProfileUpdateRequestDto.class));
    }

    @Test
    void updateUserProfile_notUser() throws Exception {

        UserProfileUpdateRequestDto requestDto = new UserProfileUpdateRequestDto("NEW:TEST");
        String body = objectMapper.writeValueAsString(requestDto);

        willThrow(new CustomGuideException(ErrorCode.USER_NOT_FOUND))
                .given(userService).updateUserProfile(anyString(), eq(requestDto));

        mockMvc.perform(patch("/api/user/profile")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.USER_NOT_FOUND.getCode()));
    }

    @Test
    void updateUserProfile_validPasswordDto() throws Exception {

        UserProfileUpdateRequestDto requestDto = new UserProfileUpdateRequestDto(null);
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/api/user/profile")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists());

        then(userService).should(never()).updateUserProfile(anyString(), any());

    }
}