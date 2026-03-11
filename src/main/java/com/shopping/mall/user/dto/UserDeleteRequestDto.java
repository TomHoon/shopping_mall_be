package com.shopping.mall.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDeleteRequestDto(@NotBlank(message = "비밀번호를 입력하세요.") String deletePassword) { }
