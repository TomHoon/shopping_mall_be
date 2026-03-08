package com.shopping.mall.guide.service;

import com.shopping.mall.guide.entity.GuideUser;
import com.shopping.mall.guide.repository.GuideUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuideUserService {

	private final GuideUserRepository guideUserRepository;

	public Long createUser(String name, String email) {
		GuideUser user = new GuideUser(name, email);
		GuideUser saved = guideUserRepository.save(user);
		return saved.getId();
	}

	public GuideUser getUser(Long id) {
		return guideUserRepository.findById(id).orElseThrow();
	}
}