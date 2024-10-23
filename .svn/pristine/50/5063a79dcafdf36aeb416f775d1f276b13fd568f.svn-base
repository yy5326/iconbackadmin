package com.golf.chat.vo;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

//카카오 조회용
public @Data class CbCommonVO {
	
	@ApiModelProperty(position = 1, notes = "사용자ID")
	private String userId;

	@ApiModelProperty(position = 2, notes = "챗봇접수번호")
	private String rcpNumber;

	@ApiModelProperty(position = 3, notes = "결제주문번호")
	private String orderId;

	@ApiModelProperty(position = 4, notes = "편도 : S / 왕복 : R")
	private String srType;

	@ApiModelProperty(position = 5, notes = "골프장명")
	private String golfName;
	
	@ApiModelProperty(position = 6, notes = "카카오 결제고유ID ")
	private String tid;
	
	@ApiModelProperty(position = 7, notes = "페이지 넘버")
	private int pageNum;
	
	@ApiModelProperty(position = 8, notes = "페이징 아이템 갯수")
	private int pageItemNum;
	
	@ApiModelProperty(position = 9, notes = "회사구분")
	private String rcpKind;
	
	public String getRcpKind() {
		if(this.rcpKind==null) {this.rcpKind="1";}
		return rcpKind;
	}
	
	public void setRcpKind(String rcpKind) {
		this.rcpKind = rcpKind;
	}
}
