package com.shopping.mall.common.error;

public enum ErrorCode {

	BAD_REQUEST(400, "잘못된 요청"),
	UNAUTHORIZED(401, "인증 필요"),
	FORBIDDEN(403, "접근 거부"),
	NOT_FOUND(404, "리소스 없음"),
	INTERNAL_SERVER_ERROR(500, "서버 오류");

	private final int code;
	private final String message;

	ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
