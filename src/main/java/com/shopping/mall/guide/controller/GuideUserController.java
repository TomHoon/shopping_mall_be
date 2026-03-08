package com.shopping.mall.guide.controller;

import com.shopping.mall.guide.dto.CreateGuideUserRequest;
import com.shopping.mall.guide.entity.GuideUser;
import com.shopping.mall.guide.service.GuideUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guide/users")
public class GuideUserController {

	private final GuideUserService guideUserService;

	@PostMapping
	public Long createUser(@Valid @RequestBody CreateGuideUserRequest request) {
		return guideUserService.createUser(request.getName(), request.getEmail());
	}

	@GetMapping("/{id}")
	public GuideUser getUser(@PathVariable Long id) {
		return guideUserService.getUser(id);
	}
}
