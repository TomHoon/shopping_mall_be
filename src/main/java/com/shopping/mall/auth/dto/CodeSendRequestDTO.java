package com.shopping.mall.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CodeSendRequestDTO(
        @NotBlank(message = "이메일을 입력하세요.") String email
) {
}
