package com.golf.enums;

import lombok.Getter;

@Getter
public enum RcpInfStat {

	/*
	 * 골프접수
	 *
	 */
	RECEIPT("0"),
	/*
	 * 픽업요청
	 *
	 */
    PICKUP("1"),
    /*
	 * 택배접수
	 *
	 */
	DELIVERY("2"),
	/*
	 * 배송완료
	 *
	 */
	COMPLETE("3"),
	/*
	 * 접수대기(카카오, 어플에서 접수받은 건)
	 *
	 */
	WAIT("4"),
	/*
	 * 골프취소
	 *
	 */
	RECEIPT_CANCEL("7"),
	/*
	 * 픽업취소
	 *
	 */
	PICKUP_CANCEL("8"),
	/*
	 * 택배취소
	 *
	 */
	DELIVERY_CANCEL("9");

	private final String value;

    RcpInfStat(String value){
        this.value = value;
    }

    public String code(){
        return value;
    }
}
