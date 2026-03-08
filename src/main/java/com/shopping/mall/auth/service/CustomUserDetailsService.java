package com.shopping.mall.auth.service;

import com.shopping.mall.auth.CustomUserDetails;
import com.shopping.mall.user.entity.User;
import com.shopping.mall.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userMapper.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}

		return new CustomUserDetails(user);
	}
}
