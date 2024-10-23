package com.golf.model;


/**
 * 공통 게시판 - 첨부파일 모델
 * @author wimy
 *
 */
public class BoardAttachVO implements GeneralModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7969376718825926103L;

	private long attachSeq;

    private long boardSeq;

    private String fileName;

    private String physicalName;

    private long ordering;

    
    
    public long getAttachSeq() {
        return attachSeq;
    }

    public void setAttachSeq(long attachSeq) {
        this.attachSeq = attachSeq;
    }

    public long getBoardSeq() {
        return boardSeq;
    }

    public void setBoardSeq(long boardSeq) {
        this.boardSeq = boardSeq;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPhysicalName() {
        return physicalName;
    }

    public void setPhysicalName(String physicalName) {
        this.physicalName = physicalName;
    }

    public long getOrdering() {
        return ordering;
    }

    public void setOrdering(long ordering) {
        this.ordering = ordering;
    }
}