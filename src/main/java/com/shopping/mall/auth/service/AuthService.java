package com.shopping.mall.auth.service;

import com.shopping.mall.auth.dto.LoginRequestDTO;
import com.shopping.mall.auth.dto.LoginResponseDTO;
import com.shopping.mall.auth.dto.RegisterRequestDTO;
import com.shopping.mall.common.JwtUtil;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public LoginResponseDTO login(LoginRequestDTO request) {

		// 1. 사용자 조회
		User user = userMapper.findByEmail(request.getEmail());

		if (user == null) {
			throw new RuntimeException("User not found");
		}

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Password mismatch");
		}

		String role = userMapper.findUserRole(user.getId());

		// 3. JWT 생성
		String accessToken = jwtUtil.createToken(user.getEmail(), role);
		String refreshToken = jwtUtil.createRefreshToken(user.getEmail());

		return new LoginResponseDTO(accessToken, refreshToken);
	}

	public void register(RegisterRequestDTO request) {

		User user = new User();

		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setStatus(UserStatus.ACTIVE);

		userMapper.saveUser(user);

	}
}
