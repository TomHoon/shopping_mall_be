package com.shopping.mall.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/*
    회원 정보를 수정하기 위해 받아야 하는 필드
 */
@Builder
public record UserProfileUpdateRequestDto(
        @NotBlank String name
) {

}