package com.golf.service.model;

public enum StatusPayVO{

	/*
	 * 임금대기
	 *
	 */
	stay("0"),
	/*
	 * 결제완료
	 *
	 */
    Complete("1"),
    /*
	 * 결제취소
	 *
	 */
	Cancel("9");

	String value;

    private StatusPayVO(String value){
        this.value = value;
    }

    public String code(){
        return value;
    }
}
