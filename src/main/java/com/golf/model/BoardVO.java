package com.golf.model;

import java.util.Date;

import com.golf.model.common.MailAddressVO;
import com.golf.model.common.PhoneVO;

import lombok.Getter;
import lombok.Setter;

/**
 * 공통 게시판 모델
 * @author wimy
 *
 */
public class BoardVO implements GeneralModel {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -48756729541924394L;

	@Getter @Setter
	private long boardSeq;
	
	@Getter @Setter
    private String subject;

	@Getter @Setter
    private String pwdYn;

	@Getter @Setter
    private String pwd;

	@Getter @Setter
    private long readCount;

	@Getter @Setter
    private String ip;

	@Getter @Setter
    private String noticeYn;

	@Getter @Setter
    private String displayYn;

	@Getter @Setter
    private String regId;
    
	@Getter @Setter
    private String regNm;

	@Getter @Setter
    private Date regDt;

	@Getter @Setter
    private String modId;
    
	@Getter @Setter
    private String modNm;

	@Getter @Setter
    private Date modDt;

	@Getter @Setter
    private String body;
    
	@Getter @Setter
    private String name;
    
	@Getter @Setter
    private String answerTp;
    
	@Getter @Setter
    private String agreeYn;
    
	@Getter @Setter
    private PhoneVO	phoneNo;
    
	@Getter @Setter
    private MailAddressVO mailAddress;
    
}