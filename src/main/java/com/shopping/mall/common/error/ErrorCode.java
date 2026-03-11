package com.shopping.mall.common.error;

public enum ErrorCode {


    // user
    USER_NOT_FOUND(404, "존재하지 않는 사용자"),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않음"),
    SAME_PASSWORD_NOT_ALLOWED(400, "기존의 비밀번호로 변경 불가"),
    USER_INACTIVE(400, "탈퇴하거나 비활성화된 회원"),
    USER_SUSPENDED(400, "정지된 회원"),

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
