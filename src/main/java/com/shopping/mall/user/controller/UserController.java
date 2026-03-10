package com.shopping.mall.user.controller;

import com.shopping.mall.auth.CustomUserDetails;
import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.dto.UserProfileUpdateRequestDto;
import com.shopping.mall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원 정보 조회", description = "사용자 Email로 회원 정보를 조회한다.")
    @GetMapping("/api/user/profile")
    public UserProfileResponseDto getUserProfile(@AuthenticationPrincipal CustomUserDetails details) {

        return userService.getUserProfile(details.getUser().getEmail());

    }

    @Operation(summary = "회원 정보 수정", description = "사용자 Email로 회원 정보를 수정한다.")
    @PatchMapping("/api/user/profile")
    public void updateUserProfile(@AuthenticationPrincipal CustomUserDetails details, @Valid @RequestBody UserProfileUpdateRequestDto requestDto) {

        userService.updateUserProfile(details.getUser().getEmail(), requestDto);

    }
}
