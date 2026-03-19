package com.shopping.mall.auth.service;

import com.shopping.mall.auth.dto.VerifyCodeRequestDTO;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyCodeServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private VerificationService verificationService;

    @Test
    void verifyCode() {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");
        String prefix = "PREFIX:";
        String key = (prefix + requestDTO.email());
        String countKey = "verify:count:" + requestDTO.email();

        given(valueOperations.increment(countKey)).willReturn(1L);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn("123456");

        verificationService.verifyCode(requestDTO, prefix);

        then(valueOperations).should(times(1)).get(eq(key));
        then(valueOperations).should(times(1)).increment(eq(countKey));
        then(redisTemplate).should(times(1)).expire(eq(countKey), any(Duration.class));
        then(redisTemplate).should(times(1)).delete(eq(countKey));
        then(redisTemplate).should(times(1)).delete(eq(key));
    }

    @Test
    void verifyCode_redisDeleteFailed() {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");
        String prefix = "PREFIX:";
        String key = (prefix + requestDTO.email());

        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        willThrow(new RuntimeException()).given(valueOperations).get(key);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> verificationService.verifyCode(requestDTO, prefix));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VERIFY_CODE_FAILED.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.VERIFY_CODE_FAILED.getMessage());

    }

    @Test
    void verifyCode_redisCodeIsNull() {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");
        String prefix = "PREFIX:";
        String key = (prefix + requestDTO.email());

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn(null);

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> verificationService.verifyCode(requestDTO, prefix));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VERIFY_CODE_EXPIRED.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.VERIFY_CODE_EXPIRED.getMessage());

    }

    @Test
    void verifyCode_redisCodeMismatch() {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");
        String prefix = "PREFIX:";
        String key = (prefix + requestDTO.email());

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn("000000");

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> verificationService.verifyCode(requestDTO, prefix));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VERIFY_CODE_MISMATCH.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.VERIFY_CODE_MISMATCH.getMessage());

    }

    @Test
    void verifyCode_toManyRequest() {
        VerifyCodeRequestDTO requestDTO = new VerifyCodeRequestDTO("test@test.com", "123456");
        String prefix = "PREFIX:";
        String key = (prefix + requestDTO.email());
        String countKey = "verify:count:" + requestDTO.email();

        given(valueOperations.increment(countKey)).willReturn(6L);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn("000000");

        CustomGuideException exception = assertThrows(CustomGuideException.class,
                () -> verificationService.verifyCode(requestDTO, prefix));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOO_MANY_EMAIL_REQUEST.getCode());
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.TOO_MANY_EMAIL_REQUEST.getMessage());

    }
}