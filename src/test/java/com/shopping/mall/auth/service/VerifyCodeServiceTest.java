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

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(key)).willReturn("123456");

        verificationService.verifyCode(requestDTO, prefix);

        then(valueOperations).should(times(1)).get(eq(key));
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
}