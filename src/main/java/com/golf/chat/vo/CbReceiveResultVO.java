package com.golf.chat.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

public @Data class CbReceiveResultVO {
	
	@ApiModelProperty(notes = "카카오")
	private String httpStatus;
	
	@ApiModelProperty(notes = "결과값\n 200 (성공)")
	private String status;

	@ApiModelProperty(notes = "결제용묶음코드")
	private String orderId;
	
	@ApiModelProperty(notes = "챗봇접수번호")
	private String rcpNumber;
	
	@ApiModelProperty(notes = "예상운임비")
	private int cbPay;
	
	@ApiModelProperty(notes = "접수일시")
	private String regDate;
	
	@ApiModelProperty(notes = "처리 결과 상태값")
	private String resultStatus;
	
	@ApiModelProperty(notes = "접수상태 ( 접수/수정/취소 이후 현재 접수 데이터의 상태값 0:골프접수, 1:픽업요청, 2:택배접수, 7:골프취소, -1:오류)")
	private String cbReceiptStatus;
	
	@ApiModelProperty(notes = "에러시 메세지")
	private String errorMsg;

}
