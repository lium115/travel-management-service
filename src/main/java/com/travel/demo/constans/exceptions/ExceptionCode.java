package com.travel.demo.constans.exceptions;

public enum ExceptionCode {
	CONTRACT_NOT_FOUND("contract not found"),
	SETTLEMENT_INVALID("settlement invalid");

	private final String message;

	ExceptionCode(String s) {
		message = s;
	}

	public String getMessage(){
		return message;
	}
}
