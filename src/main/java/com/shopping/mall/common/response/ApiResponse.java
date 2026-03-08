package com.shopping.mall.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

	private T data;
	private String message;
	private int statusCode;

	public static <T> ApiResponse<T> success(T data) {
		ApiResponse<T> response = new ApiResponse<>(
						data, "success", 200
		);

		return response;
	}

	public static <T> ApiResponse<T> fail(String message, int statusCode) {
		ApiResponse<T> response = new ApiResponse<>(
						null, message, statusCode
		);

		return response;
	}
}