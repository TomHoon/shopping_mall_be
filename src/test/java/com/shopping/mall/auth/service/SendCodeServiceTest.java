package com.shopping.mall.auth.service;

import com.shopping.mall.auth.dto.CodeSendRequestDTO;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.common.mail.EmailUtil;
import com.shopping.mall.common.mail.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendCodeServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private EmailUtil emailUtil;

    @InjectMocks
    private VerificationService verificationService;


    @Test
    void sendCode() {
        CodeSendRequestDTO requestDTO = new CodeSendRequestDTO("test@test.com");
        String prefix = "PREFIX:";
        String countKey = "send:count:" + requestDTO.email();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(countKey)).willReturn(1L);

        verificationService.sendVerifyCode(requestDTO, prefix);

        then(valueOperations).should(times(1)).set(eq((prefix + requestDTO.email())), anyString(), eq(Duration.ofMinutes(5)));
        then(valueOperations).should(times(1)).increment(eq(countKey));
        then(redisTemplate).should(times(1)).expire(eq(countKey), any(Duration.class));
        then(emailUtil).should(times(1)).sendEmail(eq(requestDTO.email()), anyString(), anyString());
    }

    @Test
    void sendCode_sendMailFailed() {
        CodeSendRequestDTO requestDTO = new CodeSendRequestDTO("test@test.com");
        String prefix = "PREFIX:";

        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        willThrow(new RuntimeException())
                .given(emailUtil).sendEmail(eq(requestDTO.email()), anyString(), anyString());

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> verificationService.sendVerifyCode(requestDTO, prefix));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MAIL_SEND_FAILED.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.MAIL_SEND_FAILED.getMessage());
    }

    @Test
    void sendCode_toManyRequest() {
        CodeSendRequestDTO requestDTO = new CodeSendRequestDTO("test@test.com");
        String prefix = "PREFIX:";
        String countKey = "send:count:" + requestDTO.email();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        given(valueOperations.increment(countKey)).willReturn(6L);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> verificationService.sendVerifyCode(requestDTO, prefix));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOO_MANY_EMAIL_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.TOO_MANY_EMAIL_REQUEST.getMessage());
    }
}