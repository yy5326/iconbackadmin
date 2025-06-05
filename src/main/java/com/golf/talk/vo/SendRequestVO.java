package com.golf.talk.vo;

import java.util.ArrayList;

import lombok.Data;


public @Data class SendRequestVO {

	// 알림톡 수신명
	private String receiver;
	/// 알림톡 수신 연락처
	private String receiverNum;

	// 메세지 관련 템플릿 key
	private String etc1;

	// 배송구분(1-편도/2-왕복)
	private String delKind;
	// 접수번호
	private String rcpNumber;
	// 보내는분 성함, 예약자 or 성함 - 아이콘에서 접수시 접수자를 안적을 수 있으므로 세팅
	private String cusSendNm;
	// 받는분 성함(20210601 추가)
	private String cusReNm;
	// 연락처 - 아이콘에서 접수시 접수자 연락처를 안적을 수 있으므로 세팅
	private String cusSendTelNum2;
	// 골프백수량
	private Integer pdCnt;
	// 픽업지주소 - 보내는 분 주소+보내는 분 상세주소
	private String cusSendAddr;
	// 도착지주소 - 받는 분 주소+받는 분 상세주소
	private String cusReAddr;
	// 라운딩일정 or 라운딩일자 - MST TABLE
	private String gameDt;
	// 픽업희망일
	private String picReqDt;
	// 회수예정일(왕복픽업희망일) - 발송구분이 R인 row의 픽업희망일
	private String rPicReqDt;
	// 회수도착지(왕복도착지주소) - 발송구분이 R인 row의 받는 분 주소+받는 분 상세주소
	private String rCusReAddr;
	// 총합계
	private Integer PdPay;
	// 추가할증 or 할증운임
	private Integer pdEtcPay;
	// 할인 적용
	private Integer pdDiscount;

	//버튼
	private ArrayList buttonList;
	//그룹번호
	private Integer rcpSeq;
	//메세지
	private String talkMessage;
	// 운송장번호
	private String pdBarcode;
	// 접수구분(0 : 관리자, 1 : 챗봇, 2 : 어플)
	private String rcpKind;

	private int prdType;

	private int boston;

	// 캐리어 수량
	private Integer boxCnt;

	private String rcpNumbers;
}
