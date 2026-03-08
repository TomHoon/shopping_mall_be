package com.shopping.mall.common.error;

public class CustomGuideException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomGuideException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode.getCode();
	}
}