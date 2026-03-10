package com.shopping.mall.user.controller;

import com.shopping.mall.auth.CustomUserDetails;
import com.shopping.mall.user.dto.UserDeleteRequestDto;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserDeleteControllerTest {

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
    void deleteUser_success() throws Exception {

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto("test!@#");
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/api/user/delete")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(eq(customUserDetails.getUser().getEmail()), any(UserDeleteRequestDto.class));
    }

    @Test
    void deleteUser_validPasswordDto() throws Exception {

        UserDeleteRequestDto requestDto = new UserDeleteRequestDto(null);
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/api/user/delete")
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

        verify(userService, never()).deleteUser(anyString(), any());

    }
}