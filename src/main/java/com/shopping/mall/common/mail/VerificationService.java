package com.shopping.mall.common.mail;

import com.shopping.mall.auth.dto.CodeSendRequestDTO;
import com.shopping.mall.auth.dto.VerifyCodeRequestDTO;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final EmailUtil emailUtil;
    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public void sendVerifyCode(CodeSendRequestDTO requestDTO, String prefix) {
        String code = String.format("%06d", secureRandom.nextInt(1000000));

        try {
            redisTemplate.opsForValue().set(
                    prefix + requestDTO.email(),
                    code,
                    Duration.ofMinutes(5)
            );

            emailUtil.sendEmail(requestDTO.email(), "[MUSINSA] 인증번호 안내", "인증번호는 [" + code + "] 입니다.");
        } catch (Exception e) {
            throw new CustomGuideException(ErrorCode.MAIL_SEND_FAILED);
        }

    }

    public void verifyCode(VerifyCodeRequestDTO requestDTO, String prefix) {
        String key = prefix + requestDTO.email();
        String savedCode;

        try {
            savedCode = redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new CustomGuideException(ErrorCode.VERIFY_CODE_FAILED);
        }

        if (savedCode == null) {
            throw new CustomGuideException(ErrorCode.VERIFY_CODE_EXPIRED);
        }

        if (!requestDTO.code().equals(savedCode)) {
            throw new CustomGuideException(ErrorCode.VERIFY_CODE_MISMATCH);
        }

        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new CustomGuideException(ErrorCode.VERIFY_CODE_FAILED);
        }
    }
}
