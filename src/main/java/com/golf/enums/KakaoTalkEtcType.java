package com.golf.enums;

import lombok.Getter;

@Getter
public enum KakaoTalkEtcType {
    VBANK_PAY("가상계좌안내"),
    RECEIPT("접수 안내"),
    ROUND_PAY("왕복 결제 완료 알림"),
    ONEWAY_PAY("편도 결제 완료 알림"),
    CANCEL("취소 완료 알림");

    private final String description;

    KakaoTalkEtcType(String description) {
        this.description = description;
    }
}