package com.shopping.mall.auth.service;

import com.shopping.mall.auth.dto.LoginRequestDTO;
import com.shopping.mall.auth.dto.LoginResponseDTO;
import com.shopping.mall.auth.dto.RegisterRequestDTO;
import com.shopping.mall.auth.dto.ResetPasswordRequestDTO;
import com.shopping.mall.common.JwtUtil;
import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.entity.UserStatus;
import com.shopping.mall.user.mapper.UserMapper;

import com.shopping.mall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserMapper userMapper;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

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

        checkUserStatus(user.getStatus());

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

    private void checkUserStatus(UserStatus status) {
        if (status.equals(UserStatus.INACTIVE)) {
            throw new CustomGuideException(ErrorCode.USER_INACTIVE);
        } else if (status.equals(UserStatus.SUSPENDED)) {
            throw new CustomGuideException(ErrorCode.USER_SUSPENDED);
        }
    }

    /*
        비밀번호 재설정
        requestDTO로 받은 newPassword와 newPasswordVerify가 일치하는지 확인
        레디스에 저장되어 있는 인증된 이메일인지 확인(레디스 키의 존재 여부로 파악)
        requestDTO로 받은 이메일로 DB에서 조회
        newPassword를 passwordEncoder로 변환 후 사용자 비밀번호에 저장(업데이트)
    */
    public void resetPassword(ResetPasswordRequestDTO requestDTO) {
        if (!requestDTO.newPassword().equals(requestDTO.newPasswordVerify())) {
            throw new CustomGuideException(ErrorCode.INVALID_PASSWORD);
        }

        Boolean isVerify = redisTemplate.hasKey("verify:" + requestDTO.email());
        if (Boolean.FALSE.equals(isVerify)) {
            throw new CustomGuideException(ErrorCode.UNAUTHORIZED_EMAIL);
        }

        User user = userRepository.findByEmail(requestDTO.email())
                .orElseThrow(() -> new CustomGuideException(ErrorCode.USER_NOT_FOUND));

        String newPassword = passwordEncoder.encode(requestDTO.newPassword());

        user.updatePassword(newPassword);

        redisTemplate.delete("verify:" + requestDTO.email());
    }
}
