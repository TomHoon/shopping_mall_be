package com.shopping.mall.auth.controller;

import com.shopping.mall.auth.dto.CodeSendRequestDTO;
import com.shopping.mall.auth.dto.ResetPasswordRequestDTO;
import com.shopping.mall.auth.service.AuthService;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.common.mail.VerificationService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthSendEmailControllerTest {

	@Autowired
	MockMvc mockMvc;

    @MockitoBean
    private VerificationService verificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail() throws Exception {
        CodeSendRequestDTO requestDTO = new CodeSendRequestDTO("test@test.com");

        String body = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/auth/send/email/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        then(verificationService).should(times(1)).sendVerifyCode(any(CodeSendRequestDTO.class), anyString());
    }

    @Test
    void sendEmail_validEmailDto() throws Exception {
        CodeSendRequestDTO requestDTO = new CodeSendRequestDTO(null);

        String body = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/auth/send/email/reset/password")
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

        then(verificationService).should(never()).sendVerifyCode(any(), anyString());

    }

    @Test
    void sendEmail_invalidEmail() throws Exception {
        CodeSendRequestDTO requestDTO = new CodeSendRequestDTO("test@test.com");

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.MAIL_SEND_FAILED))
                .given(verificationService).sendVerifyCode(eq(requestDTO), anyString());

        mockMvc.perform(post("/api/auth/send/email/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.MAIL_SEND_FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.MAIL_SEND_FAILED.getMessage()));

        then(verificationService).should(times(1)).sendVerifyCode(any(CodeSendRequestDTO.class), anyString());
    }

    @Test
    void sendEmail_toManyRequest() throws Exception {
        CodeSendRequestDTO requestDTO = new CodeSendRequestDTO("test@test.com");

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.TOO_MANY_EMAIL_REQUEST))
                .given(verificationService).sendVerifyCode(eq(requestDTO), anyString());

        mockMvc.perform(post("/api/auth/send/email/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.TOO_MANY_EMAIL_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOO_MANY_EMAIL_REQUEST.getMessage()));

        then(verificationService).should(times(1)).sendVerifyCode(any(CodeSendRequestDTO.class), anyString());
    }
}