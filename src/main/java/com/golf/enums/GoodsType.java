package com.golf.enums;

import lombok.Getter;

@Getter
public enum GoodsType {

    G("골프택배"),
    C("캐리어"),
    A("인천공항 배송");

    private final String name;

    GoodsType(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.name();
    }

    // 존재 여부를 확인하는 안전한 조회 메서드
    public static boolean contains(String code) {
        for (GoodsType type : GoodsType.values()) {
            if (type.name().equals(code)) return true;
        }
        return false;
    }
}
