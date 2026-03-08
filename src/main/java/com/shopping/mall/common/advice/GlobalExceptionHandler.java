package com.shopping.mall.common.advice;

import com.shopping.mall.common.error.CustomGuideException;
import com.shopping.mall.common.response.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomGuideException.class)
	public ApiResponse<?> handleException(CustomGuideException e) {

		return new ApiResponse<>(null, e.getMessage(), e.getErrorCode());
	}

	// 필수 혹은 request 잘못온 경우 발생하는 Exception을 처리합니다
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {

		String message = e.getBindingResult()
						.getFieldError()
						.getDefaultMessage();

		return ApiResponse.fail(message, 400);
	}
}