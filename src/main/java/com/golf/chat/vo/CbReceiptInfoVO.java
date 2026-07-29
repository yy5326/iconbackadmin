package com.golf.chat.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

public @Data class CbReceiptInfoVO {

	@ApiParam(value="카카오챗봇ID")
	@ApiModelProperty(position = 1, notes = "카카오챗봇ID")
	private String userId; // USER_ID - 카카오챗봇ID

	@ApiParam(value="접수자 이름(실명)")
	@ApiModelProperty(position = 2, notes = "접수자 이름(실명)")
	private String cusReceiptNm; // USER_ID - 카카오챗봇ID

	@ApiParam(value="접수자 전화번호")
	@ApiModelProperty(position = 3, notes = "접수자 전화번호")
	private String cusReceiptTelNum; // USER_ID - 카카오챗봇ID

	@ApiParam(value="배송구분")
	@ApiModelProperty(position = 4, notes = "(1:편도, 2:왕복)")
	private String delKind; // DEL_KIND - 배송구분(1:편도, 2:왕복)

	@ApiParam(value="발송구분")
	@ApiModelProperty(position = 5, notes = "(S:1차, R:2차) 편도는 S만")
	private String srType; // SR_TYPE - 발송구분 (S:1차, R:2차) 편도는 S만

	@ApiParam(value="보내는분 골프장 순번")
	@ApiModelProperty(position = 6, notes = "보내는분 골프장 순번")
	private int sendGolfSeq; // SEND_GOLF_SEQ - 보내는분 골프장 순번

	@ApiParam(value="보내는분 이름")
	@ApiModelProperty(position = 7, notes = "보내는분 이름")
	private String cusSendNm; // CUS_SEND_NM - 보내는분 이름

	@ApiParam(value="보내는분 연락처")
	@ApiModelProperty(position = 8, notes = "보내는분 연락처")
	private String cusSendTelnum; // CUS_SEND_TELNUM - 보내는분 연락처

	@ApiParam(value="보내는분우편번호")
	@ApiModelProperty(position = 9, notes = "골프장이 아닌 경우")
	private String cusSendZip; // CUS_SEND_ZIP - 보내는분우편번호(골프장이 아닌 경우)

	@ApiParam(value="보내는분주소")
	@ApiModelProperty(position = 10, notes = "골프장이 아닌 경우")
	private String cusSendAddr1; // CUS_SEND_ADDR1 - 보내는분주소(골프장이 아닌 경우)

	@ApiParam(value="보내는분상세주소")
	@ApiModelProperty(position = 11, notes = "골프장이 아닌 경우")
	private String cusSendAddr2; // CUS_SEND_ADDR2 - 보내는분상세주소(골프장이 아닌 경우)

	@ApiParam(value="보내는분도서산간구분")
	@ApiModelProperty(position = 12, notes = "골프장일 경우 (Y:도서산간지역, N: 그외)")
	private String sendIslYn; // SEND_ISL_YN - 보내는분도서산간구분 (Y:도서산간지역, N: 그외)

	// BR_STR_NM - 발송영업소명
	//private String brStrNm;
	// BR_STR_CODE - 발송영업소코드
	//private String brStrCode;

	@ApiParam(value="받는분 골프장 순번")
	@ApiModelProperty(position = 13, notes = "받는분 골프장 순번")
	private int recvGolfSeq; // RECV_GOLF_SEQ - 받는분골프장순번, GOLF_SEQ - 골프장순번

	@ApiParam(value="받는분 이름")
	@ApiModelProperty(position = 14, notes = "받는분 이름")
	private String cusReNm; // CUS_RE_NM - 받는분 이름

	@ApiParam(value="받는분 연락처")
	@ApiModelProperty(position = 15, notes = "받는분 연락처")
	private String cusReTelNum; // CUS_RE_TELNUM - 받는분 연락처

	@ApiParam(value="받는분우편번호")
	@ApiModelProperty(position = 16, notes = "골프장이 아닌 경우")
	private String cusReZip; // CUS_RE_ZIP - 받는분우편번호

	@ApiParam(value="받는분주소")
	@ApiModelProperty(position = 17, notes = "골프장이 아닌 경우")
	private String cusReAddr1; // CUS_RE_ADDR1 - 받는분 주소

	@ApiParam(value="받는분상세주소")
	@ApiModelProperty(position = 18, notes = "골프장이 아닌 경우")
	private String cusReAddr2; // CUS_RE_ADDR2 - 받는분 상세주소

	@ApiParam(value="받는분도서산간구분")
	@ApiModelProperty(position = 19, notes = "골프장일 경우 (Y:도서산간지역, N: 그외)")
	private String recvIslYn; // RECV_ISL_YN - 받는분도서산간구분 (Y:도서산간지역, N: 그외)

	// BR_END_NM - 도착영업소명 *필수값
	//private String brEndNm;
	// BR_END_CODE - 도착영업소코드 *필수값
	//private String brEndCode;

	@ApiParam(value="골프채 수량")
	@ApiModelProperty(position = 20, notes = "골프채 수량")
	private String pdGolfCnt; // PD_GOLF_CNT - 골프채 수량

	@ApiParam(value="개별단가")
	@ApiModelProperty(position = 21, notes = "개별단가 - 만원단위(ex-10만원일 경우 10), 개별단가가 각각 다를경우 | 표시로 구분한 문자열\n"
			+ "ex) 총 수량 10개에 관하여 10만원의 개별단가가 일괄적용되어 있는 경우 - 10\n"
			+ "총 수량 10개인데 개별단가가 다르게 적용되는 경우 - 1|1|1|1|1|1000|1000|1000|1000|1000")
	private String pdIndivPrice; // PD_INDIV_PRICE - 개별단가

	@ApiParam(value="골프백수량")
	@ApiModelProperty(position = 22, notes = "골프백수량")
	private int pdCnt; // PD_CNT - 수량(카카오 골프백수량)

	@ApiParam(value="보내는 곳 픽업희망일")
	@ApiModelProperty(position = 23, notes = "라운딩 시작일(YYYY-MM-DD)")
	private String picReqDt; // PIC_REQ_DT - 픽업희망일

	@ApiParam(value="물품메모")
	@ApiModelProperty(position = 24, notes = "물품메모")
	private String pdMemo; // PD_MEMO - 물품메모

	@ApiParam(value="라운딩 시작일")
	@ApiModelProperty(position = 25, notes = "라운딩 시작일(YYYY-MM-DD)")
	private String gameDt; // GAME_DT - 라운딩 시작일

	@ApiParam(value="라운딩 종료일")
	@ApiModelProperty(position = 26, notes = "라운딩 시작일(YYYY-MM-DD)")
	private String gameEndDt; // GAME_END_DT - 라운딩 시작일

	@ApiParam(value="챗봇접수번호")
	@ApiModelProperty(position = 27, notes = "챗봇접수번호")
	private String rcpNumber; // 챗봇접수번호(편도-접수번호, 왕복-그룹번호)

	@ApiParam(value="접수구분(0 : 관리자, 1 : 챗봇, 2 : 어플, 3 : 현대카드)")
	@ApiModelProperty(position = 28, notes = "접수구분(0 : 관리자, 1 : 챗봇, 2 : 어플, 3 : 현대카드, 5:, 6:팬스타크루즈)")
	private String rcpKind; // 챗봇접수번호(편도-접수번호, 왕복-그룹번호)

	@ApiParam(value="보스턴백수량")
	@ApiModelProperty(position = 29, notes = "보스턴백수량")
	private int bostonCnt; // BOSTON_CNT - 보스턴백 수량(카카오 보스턴백수량)


	@ApiParam(value="박스 수량")
	@ApiModelProperty(position = 15, notes = "수량")
	private int boxCnt; // BOX_Cnt 박스수량 5

	@ApiParam(value="박스크기")
	@ApiModelProperty(position = 16, notes = "박스크기")
	private String boxSize; // BOX_SIZE 박스크기 L|L|M|M|S

	/** 인천공항 캐리어 체크용 **/
	@ApiParam(value="터미널")
	@ApiModelProperty(position = 30, notes = "터미널")
	private String cusReType; // 1: 1여객터미널, 2:2여객터미널
	@ApiParam(value="캐리어수량")
	@ApiModelProperty(position = 31, notes = "캐리어수량")
	private int crCnt; //캐리어수량
	private int rcpSeqNo; //그룹
	private String rcpType; //

	@ApiParam(value="결제 상태")
	@ApiModelProperty(position = 32, notes = "결제 상태")
	private String payStat; // 결제상태 (0: 입금대기, 1: 결제완료, 9: 결제취소)

	@ApiParam(value="쿠폰 일련번호")
	@ApiModelProperty(position = 33, notes = "쿠폰 일련번호")
	private int couponSeq;

	@ApiParam(value="쿠폰 코드")
	@ApiModelProperty(position = 34, notes = "쿠폰 코드")
	private String couponCode;

	// 쿠폰 할인 금액
	@ApiParam(value="쿠폰 할인 금액")
	@ApiModelProperty(position = 35, notes = "쿠폰 할인 금액")
	private int couponDiscount;

	// 주문 번호
	@ApiParam(value="주문 번호")
	@ApiModelProperty(position = 36, notes = "주문 번호")
	private String orderNumber;

	// 결제 상태
	@ApiParam(value="결제 상태")
	@ApiModelProperty(position = 37, notes = "결제 상태")
	private String payStatus;

	// 배송 건 별 결제 금액, 수정 시에만 사용
	@ApiParam(value="배송 건 별 결제 금액")
	@ApiModelProperty(position = 38, notes = "배송 건 별 결제 금액")
	private Integer diTotalPay;

	// 취소 사유
	@ApiParam(value="취소 사유")
	@ApiModelProperty(position = 39, notes = "취소 사유")
	private String canReason;

	// 가상계좌 결제금액
	@ApiParam(value="가상계좌 결제금액")
	@ApiModelProperty(position = 40, notes = "가상계좌 결제금액")
	private String vbankPayAmt;

	// 가상계좌
	@ApiParam(value="가상계좌")
	@ApiModelProperty(position = 41, notes = "가상계좌")
	private String vbankInfo;

	// 입금만료일시
	@ApiParam(value="입금만료일시")
	@ApiModelProperty(position = 42, notes = "입금만료일시")
	private String vbankExpDtm;

	@ApiParam(value="결제 수단")
	@ApiModelProperty(position = 43, notes = "결제 수단")
	private String payMethod; // 결제 수단
}
