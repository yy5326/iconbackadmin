package com.golf.service.model;

import com.golf.model.GeneralModel;

import lombok.Data;

//접수 정보
public @Data class GolfReceiptMstVO implements GeneralModel {

	private static final long serialVersionUID = 5835188151523792726L;

	//접수순번
	private int rcpSeq;
	//배송구분
	private String delKind;
	//운임체계
	private String fareType;
	//골프장순번
	private int golfSeq = 0;
	//경기일
	private String gameDt;
	//등록자
	private String regUser;
	//등록일
	private String regDate;
	//사용여부 
	private String useYn;
	//경기종료일
	private String gameEndDt;
	//제휴사
	private String partner;
}
