package com.shopping.mall.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(
        @NotBlank(message = "이메일을 입력하세요.") String email,
        @NotBlank(message = "새 비밀번호를 입력하세요.") String newPassword,
        @NotBlank(message = "새 비밀번호를 한 번 더 입력하세요.") String newPasswordVerify
) {
}
