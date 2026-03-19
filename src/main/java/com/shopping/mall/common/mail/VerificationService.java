package com.shopping.mall.common.mail;

import com.shopping.mall.auth.dto.CodeSendRequestDTO;
import com.shopping.mall.auth.dto.VerifyCodeRequestDTO;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {
    private final EmailUtil emailUtil;
    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    /*
        인증 코드를 레디스에 저장하고 사용자에게 이메일로 인증 코드를 전송(인증 코드 만료 시간은 5분)
    */
    public void sendVerifyCode(CodeSendRequestDTO requestDTO, String prefix) {
        String code = String.format("%06d", secureRandom.nextInt(1000000));

        try {
            redisTemplate.opsForValue().set(
                    prefix + requestDTO.email(),
                    code,
                    Duration.ofMinutes(5)
            );

            // 인증 코드 이메일 전송 비동기 실행
            CompletableFuture.runAsync(() -> {
                emailUtil.sendEmail(requestDTO.email(), "[MUSINSA] 인증번호 안내", "인증번호는 [" + code + "] 입니다.");
            }).exceptionally(e -> {
                log.error("이메일 전송 중 오류 Email: {}, Message: {}", requestDTO.email(), e.getMessage());

                redisTemplate.delete(prefix + requestDTO.email());

                return null;
            });

        } catch (Exception e) {
            log.error("이메일 전송 중 오류 Email: {}, Message: {}", requestDTO.email(), e.getMessage());
            throw new CustomGuideException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    /*
        레디스에 저장된 인증 코드와 사용자가 보낸 인증 코드가 일치하는지 확인
        확인 후 레디스에서 저장된 인증 코드 삭제
        인증된 이메일은 레디스에 10분 간 저장(10분 간 비밀번호 재설정 가능)
        저장되지 않은 이메일은 재설정 불가능
    */
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
            redisTemplate.opsForValue().set("verify:"+requestDTO.email(), "true", Duration.ofMinutes(10));
        } catch (Exception e) {
            throw new CustomGuideException(ErrorCode.VERIFY_CODE_FAILED);
        }
    }
}