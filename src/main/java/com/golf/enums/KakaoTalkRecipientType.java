package com.golf.enums;

import lombok.Getter;

@Getter
public enum KakaoTalkRecipientType {
    USER("사용자", null, null),
    MANAGER("관리자", " 01047630983", "박병준");

    private final String description;
    private final String phoneNumber;
    private final String recipentName;

    KakaoTalkRecipientType(String description, String phoneNumber, String recipentName) {
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.recipentName = recipentName;
    }
}