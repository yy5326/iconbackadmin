package com.golf.chat.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.golf.service.model.GolfFileVO;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

public @Data class CbLogVO {
	
	@ApiModelProperty(notes = "히스토리순번", hidden=true)
    private Integer hisCbSeq;
	
	@ApiModelProperty(notes = "카카오 챗봇ID")
    private String userId;
	
	@ApiModelProperty(notes = "인텐트")
    private String intent;
	
	@ApiModelProperty(notes = "발화")
    private String utterance;
	
	@ApiModelProperty(notes = "엔터티엔트리")
    private String entityEntry;
	
	@ApiModelProperty(notes = "플러스친구키")
    private String plusfriendUserKey;
	
	@ApiModelProperty(notes = "봇유저키")
    private String botUserKey;

	@ApiModelProperty(notes = "등록일자", hidden=true)
    private Date regDate;
}