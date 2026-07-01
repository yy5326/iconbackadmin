package com.golf.enums;

import com.golf.service.model.StatusPayVO;
import com.golf.service.model.StatusVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 아이콘골프 PayStatus 와 매칭
 */
@Getter
@RequiredArgsConstructor
public enum PayStatus {
    WAIT("결제대기", StatusPayVO.stay.code(), StatusVO.Wait.code()),
    PAID("결제완료", StatusPayVO.Complete.code(), StatusVO.Receipt.code()),
    CANCELLED("결제취소", StatusPayVO.Cancel.code(), StatusVO.ReceiptCancel.code());

    private final String description;
    private final String payStat;
    private final String rcpInfStat;

    public static boolean isPaidOrCancelled(PayStatus status) {
        if (PAID.equals(status) || CANCELLED.equals(status)) {
            return true;
        }
        return false;
    }
}
