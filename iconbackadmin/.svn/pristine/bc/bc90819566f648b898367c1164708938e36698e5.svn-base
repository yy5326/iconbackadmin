package com.golf.service.model;

public enum StatusVO{

	/*
	 * 골프접수
	 *
	 */
	Receipt("0"),
	/*
	 * 픽업요청
	 *
	 */
    Pickup("1"),
    /*
	 * 택배접수
	 *
	 */
	Delivery("2"),
	/*
	 * 배송완료
	 *
	 */
	Complete("3"),
	/*
	 * 접수대기(카카오, 어플에서 접수받은 건)
	 *
	 */
	Wait("4"),
	/*
	 * 골프취소
	 *
	 */
	ReceiptCancel("7"),
	/*
	 * 픽업취소
	 *
	 */
	PickupCancel("8"),

	/*
	 * 택배취소
	 *
	 */
	DeliveryCancel("9");

	String value;

    private StatusVO(String value){
        this.value = value;
    }

    public String code(){
        return value;
    }
}
