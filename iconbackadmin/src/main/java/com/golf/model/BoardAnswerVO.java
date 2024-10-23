package com.golf.model;

import java.util.Date;


/**
 * 공통 게시판용 답변 모델
 * @author wimy
 *
 */
public class BoardAnswerVO implements GeneralModel {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 8986072526629824565L;

	private long answerSeq;

    private long boardSeq;

    private String subject;

    private String regId;

    private Date regDt;

    private String modId;

    private Date modDt;

    private String body;
    
    private String regNm;
    
    private String modNm;

    
    
    public long getAnswerSeq() {
        return answerSeq;
    }

    public void setAnswerSeq(long answerSeq) {
        this.answerSeq = answerSeq;
    }

    public long getBoardSeq() {
        return boardSeq;
    }

    public void setBoardSeq(long boardSeq) {
        this.boardSeq = boardSeq;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public Date getRegDt() {
        return regDt;
    }

    public void setRegDt(Date regDt) {
        this.regDt = regDt;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public Date getModDt() {
        return modDt;
    }

    public void setModDt(Date modDt) {
        this.modDt = modDt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

	public String getRegNm() {
		return regNm;
	}

	public void setRegNm(String regNm) {
		this.regNm = regNm;
	}

	public String getModNm() {
		return modNm;
	}

	public void setModNm(String modNm) {
		this.modNm = modNm;
	}
    
    
}