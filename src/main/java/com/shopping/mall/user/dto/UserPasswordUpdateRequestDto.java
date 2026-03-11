package com.shopping.mall.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateRequestDto(
        @NotBlank(message = "기존의 비밀번호를 입력하세요.") String currentPassword,
        @NotBlank(message = "새로운 비밀번호를 입력하세요.") String newPassword,
        @NotBlank(message = "비밀번호를 재입력하세요.") String newPasswordConfirm
) {

}
