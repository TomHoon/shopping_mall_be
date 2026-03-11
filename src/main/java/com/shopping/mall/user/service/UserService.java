package com.shopping.mall.user.service;

import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserDeleteRequestDto;
import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.dto.UserProfileUpdateRequestDto;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.mapper.UserMapper;
import com.shopping.mall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
        토큰에 들어있는 사용자의 이메일로 DB에서 일치하는 사용자 조회.
     */
    public UserProfileResponseDto getUserProfile(String email) {

        return userMapper.findByUserEmail(email)
                .orElseThrow(() -> new CustomGuideException(ErrorCode.USER_NOT_FOUND));

    }

    /*
        토큰에 들어있는 사용자의 이메일로 DB에서 일치하는 사용자 수정.
     */
    @Transactional
    public void updateUserProfile(String email, UserProfileUpdateRequestDto requestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomGuideException(ErrorCode.USER_NOT_FOUND));

        user.updateUser(requestDto);

    }
  
    /*
        토큰에 들어있는 사용자의 이메일로 DB에서 사용자 조회 후 비밀번호 일치 시 회원 탈퇴.(Soft Delete)
     */
    @Transactional
    public void deleteUser(String email, UserDeleteRequestDto requestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomGuideException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.deletePassword(), user.getPassword())) {
            throw new CustomGuideException(ErrorCode.INVALID_PASSWORD);
        }

        user.deleteUser();

    }
}
