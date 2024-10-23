package com.golf.service.model;

import java.util.Date;

import java.util.List;

import com.golf.model.common.BaseFileVO;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 모델
 * 
 * @author KHS
 *
 */
public @Data class GolfFileVO extends BaseFileVO {
	
	private static final long serialVersionUID = 2528987364294321314L;

	private List<MultipartFile> multiFile;
	
	private long fileSeq;
	
	private String rcpInfSeq;

	private String fileCode;
	
	private String fileNm;
	
	private String fileSvNm;
	
	private String fileContType;
	
	private long fileSize = 0;

	private String regUser;
	
	private Date regDate;
	
	private String useYn;

	private String rtNumber;
	
	/**
	 * 업로드 객체 배열
	 * @return
	 */
	public List<MultipartFile> getMultiFile() {
		return multiFile;
	}

	/**
	 * 업로드 객체 배열
	 * @param uploadRequired
	 */
	public void setMultiFile(List<MultipartFile> multiFile) {
		this.multiFile = multiFile;
	}
}
