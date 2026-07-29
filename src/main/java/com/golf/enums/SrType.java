package com.golf.enums;

import lombok.Getter;

@Getter
public enum SrType {
    S("S", "편도/왕복1차"),
    R("R", "왕복2차");

    private final String code;
    private final String label;

    SrType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static SrType fromCode(String code) {
        for (SrType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("알 수 없는 SrType 코드: " + code);
    }
}