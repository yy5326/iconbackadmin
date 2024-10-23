package com.golf.service.model;

import com.golf.model.GeneralModel;
import lombok.Data;
import com.golf.model.common.SearchVO;
import com.golf.model.common.PhoneVO;

import java.util.ArrayList;
import java.util.List;

//골프 히스토리
public @Data class GolfHistoryVO extends SearchVO {

	private static final long serialVersionUID = 5835188151523792726L;

	private long hisSeq;
	private String rcpNumber;
	private String regUser;
	private String historyMemo;
	//골프접수번호리스트
    private List<String> rcpNumberArray = new ArrayList<String>();

}
