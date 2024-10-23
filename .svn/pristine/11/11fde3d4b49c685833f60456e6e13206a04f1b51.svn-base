package com.golf.service.model;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

public @Data class UsedClubInfoVO {	
	
	@ApiParam(value="유저 구분")
	@ApiModelProperty(position = 1, notes = "유저 ID")
	private String userId; // USER_ID - 유저 ID
	
	@ApiParam(value="배송구분")
	@ApiModelProperty(position = 2, notes = "(1:편도, 2:왕복)")
	private String delKind; // DEL_KIND - 편도 고정(1)
	
	@ApiParam(value="발송구분")
	@ApiModelProperty(position = 3, notes = "(S:1차, R:2차) 편도는 S만")
	private String srType; // SR_TYPE - 발송구분 편도 고정(S)
	
	@ApiParam(value="접수자 명")
	@ApiModelProperty(position = 4, notes = "접수자명")
	private String cusSendNm; // CUS_SEND_NM - 접수자명
	
	@ApiParam(value="접수자 연락처")
	@ApiModelProperty(position = 5, notes = "접수자 연락처")
	private String cusSendTelnum; // CUS_SEND_TELNUM - 접수자 연락처
	
	@ApiParam(value="접수자 우편번호")
	@ApiModelProperty(position = 6, notes = "접수자 우편번호")
	private String cusSendZip; // CUS_SEND_ZIP - 접수자 우편번호	
	
	@ApiParam(value="접수자 주소")
	@ApiModelProperty(position = 7, notes = "접수자 주소")
	private String cusSendAddr1; // CUS_SEND_ADDR1 - 접수자 주소
	
	@ApiParam(value="접수자 상세주소")
	@ApiModelProperty(position = 8, notes = "접수자 상세주소")
	private String cusSendAddr2; // CUS_SEND_ADDR2 - 접수자 상세주소
	
	@ApiParam(value="보내는분도서산간구분")
	@ApiModelProperty(position = 9, notes = "골프장일 경우 (Y:도서산간지역, N: 그외)")
	private String sendIslYn; // SEND_ISL_YN - 보내는분도서산간구분 (Y:도서산간지역, N: 그외)
	
	// BR_STR_NM - 발송영업소명
	//private String brStrNm;
	// BR_STR_CODE - 발송영업소코드
	//private String brStrCode;
	
	@ApiParam(value="받는분 이름")
	@ApiModelProperty(position = 10, notes = "받는분 이름")
	private String cusReNm; // CUS_RE_NM - 받는분 이름 // 고촌 고정
	
	@ApiParam(value="받는분 연락처")
	@ApiModelProperty(position = 11, notes = "받는분 연락처")
	private String cusReTelNum; // CUS_RE_TELNUM - 받는분 연락처 // 고촌 고정	
	
	@ApiParam(value="받는분 우편번호")
	@ApiModelProperty(position = 12, notes = "골프장이 아닌 경우")
	private String cusReZip; // CUS_RE_ZIP - 받는분우편번호	// 고촌 고정
	
	@ApiParam(value="받는분 주소")
	@ApiModelProperty(position = 13, notes = "골프장이 아닌 경우")
	private String cusReAddr1; // CUS_RE_ADDR1 - 받는분 주소 // 고촌 고정
	
	@ApiParam(value="받는분 상세주소")
	@ApiModelProperty(position = 14, notes = "골프장이 아닌 경우")
	private String cusReAddr2; // CUS_RE_ADDR2 - 받는분 상세주소 // 고촌 고정
	
	
	// BR_END_NM - 도착영업소명 *필수값
	//private String brEndNm;
	// BR_END_CODE - 도착영업소코드 *필수값
	//private String brEndCode;	
	
	@ApiParam(value="박스 수량")
	@ApiModelProperty(position = 15, notes = "수량")
	private int boxCnt; // BOX_Cnt 박스수량 5
	
	@ApiParam(value="박스크기")
	@ApiModelProperty(position = 16, notes = "박스크기")
	private String boxSize; // BOX_SIZE 박스크기 L|L|M|M|S
		
	@ApiParam(value="픽업희망일")
	@ApiModelProperty(position = 17, notes = "픽업희망일")
	private String picReqDt; // PIC_REQ_DT - 픽업희망일
	
	@ApiParam(value="물품메모")
	@ApiModelProperty(position = 18, notes = "물품메모")
	private String pdMemo; // PD_MEMO - 물품메모
	
	@ApiParam(value="접수구분(0 : 관리자, 1 : 챗봇, 2 : 어플, 3 : 현대카드)")
	@ApiModelProperty(position = 19, notes = "접수구분(0 : 관리자, 1 : 챗봇, 2 : 어플, 3 : 현대카드)")
	private String rcpKind; // 챗봇접수번호(편도-접수번호, 왕복-그룹번호)
	
}
