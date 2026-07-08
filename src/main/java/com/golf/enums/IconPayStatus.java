package com.golf.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 아이콘골프 PayStatus 와 매칭
 */
@Getter
@RequiredArgsConstructor
public enum IconPayStatus {
    UNPAID("미결제", PayStat.UNPAID.code(), RcpInfStat.WAIT.code()),
    WAIT("결제대기", PayStat.STAY.code(), RcpInfStat.WAIT.code()),
    PAID("결제완료", PayStat.COMPLETE.code(), RcpInfStat.RECEIPT.code()),
    CANCELLED("결제취소", PayStat.CANCEL.code(), RcpInfStat.RECEIPT_CANCEL.code());

    private final String description;
    private final String payStat;
    private final String rcpInfStat;

    public static boolean isPaidOrCancelled(IconPayStatus status) {
        if (PAID.equals(status) || CANCELLED.equals(status)) {
            return true;
        }
        return false;
    }
}
