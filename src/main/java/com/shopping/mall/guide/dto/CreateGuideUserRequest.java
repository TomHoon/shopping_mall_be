package com.shopping.mall.guide.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateGuideUserRequest {

	@NotBlank(message = "name은 필수입니다")
	private String name;

	@Email(message = "email 형식이 아닙니다")
	private String email;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
