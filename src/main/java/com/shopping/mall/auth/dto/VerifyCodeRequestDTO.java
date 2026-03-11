package com.shopping.mall.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyCodeRequestDTO(
        @NotBlank(message = "이메일을 입력하세요.") String email,
        @NotBlank(message = "인증 코드를 입력하세요.") String code
) {
}
