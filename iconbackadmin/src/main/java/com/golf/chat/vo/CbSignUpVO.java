package com.golf.chat.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.golf.service.model.GolfFileVO;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

public @Data class CbSignUpVO {	
	//사용자 ID
	@ApiParam(required=true)
	@ApiModelProperty(notes = "사용자ID")
	private String userId;
	
	//전화번호
	@ApiParam(required=true)
	@ApiModelProperty(notes = "사용자 전화번호")
	private String userTelNum;
	
	//싱크일시
	@ApiModelProperty(notes = "싱크일시")
	private String userSyncedDate;
	
	//약관동의태그배열
	@ApiParam(required=true)
	@ApiModelProperty(notes = "약관동의태그")
    private String[] userTag;
    
	//약관동의일시배열
	@ApiParam(required=true)
	@ApiModelProperty(notes = "약관동의일시")
    private String[]userAgreedDate;
	
	//약관동의태그
	@ApiModelProperty(hidden=true)
	private String userTagArray;
	
	//약관동의일시
	@ApiModelProperty(hidden=true)
	private String userAgreedDateArray;
}
