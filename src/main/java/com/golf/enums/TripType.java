package com.golf.enums;

import lombok.Getter;

@Getter
public enum TripType {
    ONE_WAY("1", "편도"),
    ROUND_TRIP("2", "왕복");

    private final String code;
    private final String label;

    TripType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static TripType fromCode(String code) {
        for (TripType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("알 수 없는 receiptTrip 코드: " + code);
    }
}