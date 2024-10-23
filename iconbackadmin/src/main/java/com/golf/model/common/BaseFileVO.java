package com.golf.model.common;

import org.apache.commons.lang.StringUtils;

import com.golf.model.GeneralModel;

/**
 * 파일 업로드 처리용 기본 모델
 * 
 * @author wimy 
 */
public class BaseFileVO implements GeneralModel {

	private static final long serialVersionUID = -7405043358592644161L;

	private boolean isDeleted;
	
	//private long refSeq = -1l;	
	private String refSeq = "0";
	

	
	/** 파일명 */
	private String fileName = "";
	/** ContextType */
	private String contentType = "";
	/** 하위 디렉토리 지정 */
	private String serverSubPath = "";
	/** 물리적 파일명 */
	private String physicalName = "";
	/** 파일 사이즈 */
	private long size = 0L;
	//for json
	private String fullPathName = "";
	//for json
	private String ext = "";
	
	
	/**
	 * 참조 순번 - VARCHAR, NUMBER 모두 사용
	 * NUMBER일 경우 형 변환 필요
	 * @return
	 */
	public String getRefSeq() {
		return this.refSeq;
	}
	
	/**
	 * 참조 순번 - VARCHAR, NUMBER 모두 사용
	 * NUMBER일 경우 형 변환 필요
	 * @param refSeq
	 */
	public void setRefSeq(String refSeq) {
		this.refSeq = refSeq;
	}
	

	/**
	 * 원본 파일명
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 원본 파일명
	 * 
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 서브 디렉토리
	 * 
	 * @return the serverSubPath
	 */
	public String getServerSubPath() {
		return serverSubPath;
	}

	/**
	 * 서브 디렉토리
	 * 
	 */
	public void setServerSubPath(String serverSubPath) {
		this.serverSubPath = serverSubPath;
	}

	/**
	 * 업로드 루트 경로를 제외한 서버 저장 파일 경로 + 파일명
	 * 
	 * @return the physicalName
	 */
	public String getPhysicalName() {
		return physicalName;
	}

	/**
	 * 업로드 루트 경로를 제외한 서버 저장 파일 경로 + 파일명
	 * 
	 * @param physicalName
	 *            the physicalName to set
	 */
	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	/**
	 * size attribute를 리턴한다.
	 * 
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * size attribute 값을 설정한다.
	 * 
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}
	
	
	
	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * [읽기전용] 업로드 파일이 저장된 경로와 파일명을 합쳐서 가져옵니다. 
	 *  - 첨부파일 신규 등록 시 사용
	 * @return
	 */
	public String getFullPathName() {
		if (StringUtils.isEmpty(this.getServerSubPath())) {
			this.fullPathName = "/" + this.getPhysicalName();
		} else {
			this.fullPathName = this.getServerSubPath() + "/" + this.getPhysicalName();
		}
		
		return this.fullPathName;
	}

	/**
	 * 단순 파일 확장자
	 * @return
	 */
	public String getExt() {
		return ext;
	}

	/**
	 * 단순 파일 확장자
	 * @param ext
	 */
	public void setExt(String ext) {
		this.ext = ext;
	}
}
