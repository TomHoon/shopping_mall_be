package com.shopping.mall.common.error;

public enum ErrorCode {


    // user
    USER_NOT_FOUND(404, "존재하지 않는 사용자"),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않음"),
    SAME_PASSWORD_NOT_ALLOWED(400, "기존의 비밀번호로 변경 불가"),
    USER_INACTIVE(400, "탈퇴하거나 비활성화된 회원"),
    USER_SUSPENDED(400, "정지된 회원"),

    // mail
    VERIFY_CODE_EXPIRED(404, "인증 코드 만료 또는 존재하지 않음"),
    VERIFY_CODE_MISMATCH(400, "인증 코드가 일치하지 않음"),
    UNAUTHORIZED_EMAIL(404, "이메일이 인증되지 않음"),
    TOO_MANY_EMAIL_REQUEST(400, "이메일 요청 5분 후 재시도 바람"),
    MAIL_SEND_FAILED(500, "메일 전송 중 서버 오류"),
    VERIFY_CODE_FAILED(500, "인증 코드 처리 중 서버 오류"),

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
