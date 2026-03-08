package com.shopping.mall.auth.controller;

import com.shopping.mall.auth.dto.LoginRequestDTO;
import com.shopping.mall.auth.dto.LoginResponseDTO;
import com.shopping.mall.auth.dto.RefreshTokenRequestDTO;
import com.shopping.mall.auth.dto.RegisterRequestDTO;
import com.shopping.mall.auth.service.AuthService;

import com.shopping.mall.common.JwtUtil;
import com.shopping.mall.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final JwtUtil jwtUtil;
	private final UserMapper userMapper;


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


}