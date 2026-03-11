package com.shopping.mall.user.controller;

import com.shopping.mall.auth.CustomUserDetails;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserPasswordUpdateRequestDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserPasswordUpdateControllerTest {

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
    void updatePassword() throws Exception {

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "test!@#",
                "newPassword",
                "newPassword"
        );
        String body = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(patch("/api/user/new-password")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk());

        then(userService).should(times(1)).updatePassword(eq(customUserDetails.getUser().getEmail()), any(UserPasswordUpdateRequestDto.class));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDtos")
    void updatePassword_validPasswordDto(UserPasswordUpdateRequestDto requestDtos) throws Exception {

        String body = objectMapper.writeValueAsString(requestDtos);

        mockMvc.perform(patch("/api/user/new-password")
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

        then(userService).should(never()).updatePassword(anyString(), any());

    }

    @Test
    void updatePassword_notUser() throws Exception {

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "test!@#",
                "newPassword",
                "newPassword"
        );
        String body = objectMapper.writeValueAsString(requestDto);

        willThrow(new CustomGuideException(ErrorCode.USER_NOT_FOUND))
                .given(userService).updatePassword(anyString(), eq(requestDto));

        mockMvc.perform(patch("/api/user/new-password")
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
    void updatePassword_invalidPassword() throws Exception {

        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "falsPassword",
                "newPassword",
                "newPassword"
        );
        String body = objectMapper.writeValueAsString(requestDto);

        willThrow(new CustomGuideException(ErrorCode.INVALID_PASSWORD))
                .given(userService).updatePassword(anyString(), eq(requestDto));

        mockMvc.perform(patch("/api/user/new-password")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PASSWORD.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.INVALID_PASSWORD.getCode()));
    }

    @Test
    void updatePassword_samePasswordNotAllowed() throws Exception {
        UserPasswordUpdateRequestDto requestDto = new UserPasswordUpdateRequestDto(
                "test!@#",
                "test!@#",
                "test!@#"
        );
        String body = objectMapper.writeValueAsString(requestDto);

        willThrow(new CustomGuideException(ErrorCode.SAME_PASSWORD_NOT_ALLOWED))
                .given(userService).updatePassword(anyString(), eq(requestDto));

        mockMvc.perform(patch("/api/user/new-password")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.message").value(ErrorCode.SAME_PASSWORD_NOT_ALLOWED.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.SAME_PASSWORD_NOT_ALLOWED.getCode()));
    }

    static Stream<UserPasswordUpdateRequestDto> provideInvalidDtos() {
        return Stream.of(
                new UserPasswordUpdateRequestDto(null, "new123!", "new123!"), // currentPassword 누락
                new UserPasswordUpdateRequestDto("old123!", null, "new123!"), // newPassword 누락
                new UserPasswordUpdateRequestDto("old123!", "new123!", null)  // confirm 누락
        );
    }
}