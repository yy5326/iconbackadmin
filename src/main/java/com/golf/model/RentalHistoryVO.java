package com.golf.model;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class RentalHistoryVO {
    private int rentalNo;
    private int rcpInfSeq;
    private int rcpSeq;
    private long orderNo;
    private String type;
    private String orderStatCd;
    private String message;
    private Timestamp regDtm;
}
