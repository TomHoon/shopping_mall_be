package com.shopping.mall.user.mapper;

import com.shopping.mall.user.dto.UserProfileResponseDto;
import com.shopping.mall.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

	User findByEmail(String email);
	void saveUser(User user);
	String findUserRole(Long userId);
	String findUserRoleByEmail(String email);

    Optional<UserProfileResponseDto> findByUserEmail(String email);
}