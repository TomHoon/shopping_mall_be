package com.shopping.mall.user.service;

import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.error.ErrorCode;
import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    /*
        토큰에 들어있는 사용자의 이메일로 DB에서 일치하는 사용자 조회.
     */
    public UserProfileResponseDto getUserProfile(String email) {

        return userMapper.findByUserEmail(email)
                .orElseThrow(() -> new CustomGuideException(ErrorCode.USER_NOT_FOUND));

    }
}
