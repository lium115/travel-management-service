package com.travel.demo.constans;

public enum PaymentStatus {
	PAID("已支付"),
	UNPAID("未支付");
	private final String description;

	PaymentStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
