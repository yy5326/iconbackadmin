package com.golf.chat.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

public @Data class CbDeliveryVO {
	
	//사용자 ID
	@ApiParam(required=true)
	@ApiModelProperty(notes = "사용자ID")
	private String userId;
	//페이지 번호
	@ApiModelProperty(notes = "페이지 넘버", hidden=true)
	private int pageNum;
	//페이징 아이템 갯수
	@ApiModelProperty(notes = "페이징 아이템 갯수", hidden=true)
	private int pageItemNum;
	@ApiModelProperty(notes = "골프접수발송순번", hidden=true)
	private int rcpInfSeq;
	
	@ApiModelProperty(notes = "골프접수번호(챗봇)")
	private String rcpNumber;
	@ApiModelProperty(notes = "골프장명")
	private String golfName;
	@ApiModelProperty(notes = "편도/왕복")
	private String delKind;
	@ApiModelProperty(notes = "발송구분, (S:1차, R:2차) 편도는 S만")
	private String srType; // SR_TYPE - 발송구분 (S:1차, R:2차) 편도는 S만
	@ApiModelProperty(notes = "접수상태")
	private String rcpInfStat;
	@ApiModelProperty(notes = "확정운임, 총물품가액")
	private int pdPay;
	@ApiModelProperty(notes = "골프백 개별단가")
	private String pdIndivPrice;
	@ApiModelProperty(notes = "결제용 묶음코드")
	private String orderId;
	@ApiModelProperty(notes = "외부엽체명")
	private String extComp;	
	@ApiModelProperty(notes = "운송장번호")
	private String pdBarcode;
	@ApiModelProperty(notes = "외부엽체연락처")
	private String extCompTelNum;
	@ApiModelProperty(notes = "택배접수일시")
	private String pdRecDt;
	@ApiModelProperty(notes = "고객 접수일시")
	private String regDate;
	@ApiModelProperty(notes = "취소사유")
	private String canReason;
	@ApiModelProperty(notes = "편도<->왕복 수정여부 Y/N")
	private String changeDeltypeYn;

	//접수기본정보
	@ApiModelProperty(notes = "접수자이름")
	private String cusReceiptNm;
	@ApiModelProperty(notes = "접수자 전화번호")
	private String cusReceiptTelNum;
	@ApiModelProperty(notes = "라운딩 시작일자")
	private String gameDt;
	@ApiModelProperty(notes = "라운딩 종료일자")
	private String gameEndDt;
	@ApiModelProperty(notes = "골프채수량")
	private String pdGolfCnt;
	@ApiModelProperty(notes = "골프백 수량")
	private String pdCnt;
	@ApiModelProperty(notes = "보스턴백 수량")
	private String bostonCnt;
	
	//보내는곳 정보 
	@ApiModelProperty(notes = "보내는곳 이름")
	private String cusSendNm;  
	@ApiModelProperty(notes = "보내는곳 연락처")
	private String cusSendTelNum;
	@ApiModelProperty(notes = "보내는곳 주소")
	private String cusSendAddr1;
	@ApiModelProperty(notes = "보내는곳 주소")
	private String cusSendAddr2; 
	@ApiModelProperty(notes = "보내는곳 우편번호")
	private String cusSendZip;
	@ApiModelProperty(notes = "픽업희망일")
	private String picReqDt;
	@ApiModelProperty(notes = "보내는곳 골프장")
	private String sendGolfSeq;
	@ApiModelProperty(position = 12, notes = "골프장일 경우 (Y:도서산간지역, N: 그외)")
	private String sendIslYn; // SEND_ISL_YN - 보내는분도서산간구분 (Y:도서산간지역, N: 그외)
	
	//받는곳 정보
	@ApiModelProperty(notes = "받는곳 이름")
	private String cusReNm; 
	@ApiModelProperty(notes = "받는곳 연락처")
	private String cusReTelNum; 
	@ApiModelProperty(notes = "받는곳 골프장명")
	private String cusReGolfName; 
	@ApiModelProperty(notes = "받는곳 주소")
	private String cusReAddr1;
	@ApiModelProperty(notes = "받는곳 주소")
	private String cusReAddr2;
	@ApiModelProperty(notes = "받는곳 우편번호")
	private String cusReZip;
	@ApiModelProperty(notes = "픽업희망일(왕복인 경우만)")
	private String rtPicReqDt;
	@ApiModelProperty(notes = "받는곳 골프장")
	private String recvGolfSeq;
	@ApiModelProperty(notes = "골프장일 경우 (Y:도서산간지역, N: 그외)")
	private String recvIslYn; // RECV_ISL_YN - 받는분도서산간구분 (Y:도서산간지역, N: 그외)
	@ApiModelProperty(notes = "메모")
	private String pdMemo;
	
	//왕복도착지 정보 
	@ApiModelProperty(notes = "왕복도착지 이름")
	private String rtCusReNm;
	@ApiModelProperty(notes = "왕복도착지 연락처")
	private String rtCusReTelNum;
	@ApiModelProperty(notes = "왕복도착지 주소")
	private String rtCusReAddr1;
	@ApiModelProperty(notes = "왕복도착지 주소")
	private String rtCusReAddr2;
	@ApiModelProperty(notes = "왕복도착지 우편번호")
	private String rtCusReZip;
	
	@ApiModelProperty(notes = "골프백사진(내부|외부)")
	private String golfPhoto;
	
}
