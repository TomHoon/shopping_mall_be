package com.shopping.mall.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserProfileResponseDto(
        Long id,
        String email,
        String name,
        UserStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {
    public static UserProfileResponseDto from(User user){
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

