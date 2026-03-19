package com.shopping.mall.auth.controller;

import com.shopping.mall.auth.dto.LoginRequestDTO;
import com.shopping.mall.auth.dto.LoginResponseDTO;
import com.shopping.mall.auth.dto.ResetPasswordRequestDTO;
import com.shopping.mall.auth.service.AuthService;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserPasswordUpdateRequestDto;
import com.shopping.mall.user.service.UserService;
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

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthResetPasswordControllerTest {

	@Autowired
	MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void resetPassword() throws Exception {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "test@test.com",
                "test!1@2",
                "test!1@2"
        );

        String body = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/auth/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        then(authService).should(times(1)).resetPassword(any(ResetPasswordRequestDTO.class));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDtos")
    void resetPassword_validPasswordDto(ResetPasswordRequestDTO requestDtos) throws Exception {

        String body = objectMapper.writeValueAsString(requestDtos);

        mockMvc.perform(post("/api/auth/reset/password")
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

        then(authService).should(never()).resetPassword(any());

    }

    static Stream<ResetPasswordRequestDTO> provideInvalidDtos() {
        return Stream.of(
                new ResetPasswordRequestDTO(null, "new123!", "new123!"), // email 누락
                new ResetPasswordRequestDTO("test@test.com", null, "new123!"), // newPassword 누락
                new ResetPasswordRequestDTO("test@test.com", "new123!", null)  // newPasswordVerify 누락
        );
    }

    @Test
    void resetPassword_invalidPassword() throws Exception {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "test@test.com",
                "test!1@2",
                "test!1@2"
        );

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.INVALID_PASSWORD))
                .given(authService).resetPassword(eq(requestDTO));

        mockMvc.perform(post("/api/auth/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.INVALID_PASSWORD.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PASSWORD.getMessage()));

        then(authService).should(times(1)).resetPassword(any(ResetPasswordRequestDTO.class));
    }

    @Test
    void resetPassword_isNotVerify() throws Exception {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "test@test.com",
                "test!1@2",
                "test!1@2"
        );

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.UNAUTHORIZED_EMAIL))
                .given(authService).resetPassword(eq(requestDTO));

        mockMvc.perform(post("/api/auth/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.UNAUTHORIZED_EMAIL.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED_EMAIL.getMessage()));

        then(authService).should(times(1)).resetPassword(any(ResetPasswordRequestDTO.class));
    }

    @Test
    void resetPassword_userNotFound() throws Exception {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(
                "test@test.com",
                "test!1@2",
                "test!1@2"
        );

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.USER_NOT_FOUND))
                .given(authService).resetPassword(eq(requestDTO));

        mockMvc.perform(post("/api/auth/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));

        then(authService).should(times(1)).resetPassword(any(ResetPasswordRequestDTO.class));
    }
}