package com.golf.enums;

import lombok.Getter;

@Getter
public enum PrdType {
	GOLF(1, "골프택배"),
	RENTAL(2, "중고채"),
	CARRIER(3, "캐리어"),
	AIRPORT(5, "인천공항배송");

	private final int value;
	private final String description;

    PrdType(int value, String description){
        this.value = value;
        this.description = description;
    }

	public static PrdType fromValue(int value) {
		for (PrdType type : values()) {
			if (type.value == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("알 수 없는 PrdType 값: " + value);
	}
}