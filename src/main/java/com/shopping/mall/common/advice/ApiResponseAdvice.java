package com.shopping.mall.common.advice;

import com.shopping.mall.common.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		String requestPath = returnType.getMethod().getDeclaringClass().getName();
		if (requestPath.contains("swagger") || requestPath.contains("openapi")) {
			return false;
		}
		// 모든 응답값 대상으로 지원한다.
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		String uri = request.getURI().getPath();

		// Exclude Swagger/OpenAPI endpoints
		if (uri.contains("/swagger") || uri.contains("/v3/api-docs") || uri.contains("/webjars")) {
			return body;
		}

		if (body instanceof ApiResponse<?>) {
			return body;
		}

		if (body instanceof String) {
			return body;
		}

		// 파일응답 예외 처리
		if (body instanceof Resource
						|| body instanceof byte[]
						|| body instanceof StreamingResponseBody) {
			return body;
		}

		return ApiResponse.success(body);
	}
}
