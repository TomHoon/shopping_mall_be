package com.shopping.mall.auth.controller;

import com.shopping.mall.auth.dto.*;
import com.shopping.mall.auth.service.AuthService;

import com.shopping.mall.common.JwtUtil;
import com.shopping.mall.common.mail.VerificationService;
import com.shopping.mall.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final VerificationService verifyService;


    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {

        return authService.login(request);

    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequestDTO request) {

        authService.register(request);

    }

    @PostMapping("/refresh")
    public LoginResponseDTO refresh(@RequestBody RefreshTokenRequestDTO request) {

        String token = request.getRefreshToken();

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtil.getSubject(token);

        String role = userMapper.findUserRoleByEmail(email);

        String newAccessToken = jwtUtil.createToken(email, role);
        String newRefreshToken = jwtUtil.createRefreshToken(email);

        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }

    @Operation(summary = "비밀번호 재설정 인증 코드 이메일 전송", description = "사용자에게 인증 코드 이메일을 전송")
    @PostMapping("/send/email/reset/password")
    public void resetPasswordSendEmail(@Valid @RequestBody CodeSendRequestDTO requestDTO) {
        verifyService.sendVerifyCode(requestDTO, "resetPassword");
    }

    @Operation(summary = "비밀번호 재설정 이메일 인증", description = "사용자가 받은 인증 코드로 이메일을 인증")
    @PostMapping("verify/code/reset/password")
    public void resetPasswordVerifyCode(@Valid @RequestBody VerifyCodeRequestDTO requestDTO) {
        verifyService.verifyCode(requestDTO, "resetPassword");
    }

    @Operation(summary = "비밀번호 재설정", description = "사용자의 새 비밀번호와 비밀번호 확인을 입력 받음")
    @PostMapping("reset/password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        authService.resetPassword(requestDTO);
    }
}