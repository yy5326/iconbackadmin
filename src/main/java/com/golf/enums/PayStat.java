package com.golf.enums;

public enum PayStat {

	/*
	 * 미결제
	 *
	 */
	UNPAID("-1"),

	/*
	 * 임금대기
	 *
	 */
	STAY("0"),
	/*
	 * 결제완료
	 *
	 */
    COMPLETE("1"),
    /*
	 * 결제취소
	 *
	 */
	CANCEL("9");

	String value;

    private PayStat(String value){
        this.value = value;
    }

    public String code(){
        return value;
    }
}
