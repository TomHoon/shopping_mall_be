package com.shopping.mall.auth.controller;

import com.shopping.mall.auth.dto.VerifyCodeRequestDTO;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.common.mail.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
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
class AuthVerifyCodeControllerTest {

	@Autowired
	MockMvc mockMvc;

    @MockitoBean
    private VerificationService verificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void verifyCode() throws Exception {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");

        String body = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/auth/verify/code/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));

        then(verificationService).should(times(1)).verifyCode(any(VerifyCodeRequestDTO.class), anyString());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDtos")
    void verifyCode_validVerifyCodeDto(VerifyCodeRequestDTO requestDTO) throws Exception {

        String body = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post("/api/auth/verify/code/reset/password")
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

        then(verificationService).should(never()).verifyCode(any(), anyString());
    }

    private static Stream<VerifyCodeRequestDTO> provideInvalidDtos() {
        return Stream.of(
                new VerifyCodeRequestDTO(null, "123456"),       // email 누락
                new VerifyCodeRequestDTO("test@test.com", null) // code 누락

        );
    }

    @Test
    void verifyCode_serverError() throws Exception {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.VERIFY_CODE_FAILED))
                .given(verificationService).verifyCode(eq(requestDTO), anyString());

        mockMvc.perform(post("/api/auth/verify/code/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.VERIFY_CODE_FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VERIFY_CODE_FAILED.getMessage()));

        then(verificationService).should(times(1)).verifyCode(any(VerifyCodeRequestDTO.class), anyString());
    }

    @Test
    void verifyCode_codeExpired() throws Exception {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.VERIFY_CODE_EXPIRED))
                .given(verificationService).verifyCode(eq(requestDTO), anyString());

        mockMvc.perform(post("/api/auth/verify/code/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.VERIFY_CODE_EXPIRED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VERIFY_CODE_EXPIRED.getMessage()));

        then(verificationService).should(times(1)).verifyCode(any(VerifyCodeRequestDTO.class), anyString());
    }

    @Test
    void verifyCode_codeMismatch() throws Exception {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.VERIFY_CODE_MISMATCH))
                .given(verificationService).verifyCode(eq(requestDTO), anyString());

        mockMvc.perform(post("/api/auth/verify/code/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.VERIFY_CODE_MISMATCH.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.VERIFY_CODE_MISMATCH.getMessage()));

        then(verificationService).should(times(1)).verifyCode(any(VerifyCodeRequestDTO.class), anyString());
    }

    @Test
    void verifyCode_toManyRequest() throws Exception {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");

        String body = objectMapper.writeValueAsString(requestDTO);

        willThrow(new CustomGuideException(ErrorCode.TOO_MANY_EMAIL_REQUEST))
                .given(verificationService).verifyCode(eq(requestDTO), anyString());

        mockMvc.perform(post("/api/auth/verify/code/reset/password")
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

        then(verificationService).should(times(1)).verifyCode(any(VerifyCodeRequestDTO.class), anyString());
    }
}


