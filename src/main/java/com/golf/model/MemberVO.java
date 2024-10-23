package com.golf.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.golf.model.common.GeneralUserVO;

/**
 * 사용자 모델 - GeneralUserVO 상속<br><br>
 * 
 * 골프 - golf 패키지 사용<br>
 * @author wimy
 *
 */
@Deprecated
public class MemberVO extends GeneralUserVO {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9005856239972765580L;

	
	
	//private String userEmail;

    //private String userTypeCd;

    //private String joinTypeCd;

    //private String userNm;

    //private String userPwd;

    private Date pwdChangeDt;
    
    private Date pwdRequestDt;

    private String reservedField;

    //private String mobile;

    private Date recentLoginDt;

    private long loginCnt;

    private String oldMemberYn;

    private String authYn;

    private String useYn;

    private String withdrawalYn;

    private String dormantYn;

    private String dupinfo;

    //private String phone;

    private String fax;

    private String zip;

    private String addr1;

    private String addr2;

    private String memo1;

    private String memo2;
    
    private String regId;

    private Date regDt;

    private String modId;

    private Date modDt;

    private String snsToken;
    
    private String useAgreeYn;
    
    private String locAgreeYn;
    
    private String mailAgreeYn;
    
    private String ownYn;
    
    private String makername;
    
    private String carname1name;
    
    private String carname2name;
    
    private String makeYear;
    
    private String mileage;
    
    private String regNm;
    
    private String modNm;
    
    
	/**
	 *  사용자 아이디 
	 *  alias for UserID
	 */
	public String getUserId() {
		return super.getUserID();
	}
	
	/**
	 * 사용자 아이디
	 * alias for UserID
	 * @param userId
	 */
	public void setUserId(String userId) {
		super.setUserID(userId);
	}
	

	/**
     * 사용자 이메일
     * @return
     */
    public String getUserEmail() {
        //return userEmail;
    	return super.getMailAddress().getAddress();
    }

    /**
     * 사용자 이메일
     * @param userEmail
     */
    public void setUserEmail(String userEmail) {
        //this.userEmail = userEmail;
    	super.getMailAddress().setAddress(userEmail);
    }
    
//    @Override
//    public String getUserID() {
//    	return this.getUserEmail();
//    };
//    
//    @Override 
//    public void setUserID(String userID) {
//    	this.setUserEmail(userID);
//    };

    

    /**
     * 이름
     * @return
     */
    public String getUserNm() {
        return super.getUserName();
    }

    /**
     * 이름
     * @param userNm
     */
    public void setUserNm(String userNm) {
        //this.userNm = userNm;
    	super.setUserName(userNm);
    }

    /**
     * 비밀번호 - SHA256
     * @return
     */
    public String getUserPwd() {
        //return userPwd;
    	return super.getPassword();
    }

    /**
     * 비밀번호 - SHA256
     * @param userPwd
     */
    public void setUserPwd(String userPwd) {
        //this.userPwd = userPwd;
    	super.setPassword(userPwd);
    }

    /**
     * 비밀번호 변경일
     * @return
     */
    public Date getPwdChangeDt() {
        return pwdChangeDt;
    }

    /**
     * 비밀번호 변경일
     * @param pwdChangeDt
     */
    public void setPwdChangeDt(Date pwdChangeDt) {
        this.pwdChangeDt = pwdChangeDt;
    }

    /**
     * 임시 필드
     * @return
     */
    public String getReservedField() {
        return reservedField;
    }

    /**
     * 임시 필드
     * @param reservedField
     */
    public void setReservedField(String reservedField) {
        this.reservedField = reservedField;
    }

    /**
     * 최근 로그인 일시
     * @return
     */
    public Date getRecentLoginDt() {
        return recentLoginDt;
    }

    /**
     * 최근 로그인 일시
     * @param recentLoginDt
     */
    public void setRecentLoginDt(Date recentLoginDt) {
        this.recentLoginDt = recentLoginDt;
    }

    /**
     * 누적 로그인 수
     * @return
     */
    public long getLoginCnt() {
        return loginCnt;
    }

    /**
     * 누적 로그인 수
     * @param loginCnt
     */
    public void setLoginCnt(long loginCnt) {
        this.loginCnt = loginCnt;
    }

    /**
     * 이전 회원 여부
     * @return
     */
    public String getOldMemberYn() {
        return oldMemberYn;
    }

    /**
     * 이전 회원 여부
     * @param oldMemberYn
     */
    public void setOldMemberYn(String oldMemberYn) {
        this.oldMemberYn = oldMemberYn;
    }
    
    /**
     * [읽기전용] 이전회원 여부
     * @return
     */
    public boolean isOldMember() {
    	if(!StringUtils.isEmpty(this.getOldMemberYn())) {
    		if(this.getOldMemberYn().toUpperCase().equals("Y"))
    			return true;
    		else
    			return false;
    	}
    	else
    		return false;
    }
    

    /**
     * 인증 여부
     * @return
     */
    public String getAuthYn() {
        return authYn;
    }

    /**
     * 인증 여부
     * @param authYn
     */
    public void setAuthYn(String authYn) {
        this.authYn = authYn;
    }
    
    /**
     * [읽기전용] 인증 여부
     * @return
     */
    public boolean isAuth() {
    	if(!StringUtils.isEmpty(this.getAuthYn())) {
    		if(this.getAuthYn().toUpperCase().equals("Y"))
    			return true;
    		else
    			return false;
    	}
    	else
    		return false;
    }
    
    
    /**
     * 사용 여부
     * @return
     */
    public String getUseYn() {
        return useYn;
    }

    /**
     * 사용 여부
     * @param useYn
     */
    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }
    
    /**
     * [읽기전용] 사용 여부
     * @return
     */
    public boolean isUse() {
    	if(!StringUtils.isEmpty(this.getUseYn())) {
    		if(this.getUseYn().toUpperCase().equals("Y"))
    			return true;
    		else
    			return false;
    	}
    	else
    		return false;
    }    

    /**
     * 탈퇴여부
     * @return
     */
    public String getWithdrawalYn() {
        return withdrawalYn;
    }

    /**
     * 탈퇴여부
     * @param withdrawalYn
     */
    public void setWithdrawalYn(String withdrawalYn) {
        this.withdrawalYn = withdrawalYn;
    }
    
    /**
     * [읽기전용] 탈퇴 여부
     * @return
     */
    public boolean isWithdrawal() {
    	if(!StringUtils.isEmpty(this.getWithdrawalYn())) {
    		if(this.getWithdrawalYn().toUpperCase().equals("Y"))
    			return true;
    		else
    			return false;
    	}
    	else
    		return false;
    } 
    
    /**
     * 휴면 회원 여부
     * @return
     */
    public String getDormantYn() {
        return dormantYn;
    }

    /**
     * 휴면 회원 여부
     * @param dormantYn
     */
    public void setDormantYn(String dormantYn) {
        this.dormantYn = dormantYn;
    }

    /**
     * [읽기전용] 휴면 회원 여부
     * @return
     */
    public boolean isDormant() {
    	if(!StringUtils.isEmpty(this.getDormantYn())) {
    		if(this.getDormantYn().toUpperCase().equals("Y"))
    			return true;
    		else
    			return false;
    	}
    	else
    		return false;
    } 
    
    /**
     * 아이핀 dupinfo
     * @return
     */
    public String getDupinfo() {
        return dupinfo;
    }

    /**
     * 아이핀 dupinfo
     * @param dupinfo
     */
    public void setDupinfo(String dupinfo) {
        this.dupinfo = dupinfo;
    }

    
    /**
     * 팩스 
     * 주의 : PhoneVO 모델 적용 안함
     * @return
     */
    public String getFax() {
        return fax;
    }

    /**
     * 팩스
     * 주의 : PhoneVO 모델 적용 안함
     * @param fax
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getMemo1() {
        return memo1;
    }

    public void setMemo1(String memo1) {
        this.memo1 = memo1;
    }

    public String getMemo2() {
        return memo2;
    }

    public void setMemo2(String memo2) {
        this.memo2 = memo2;
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

    /**
     * SNS 토큰 : 미정
     * @return
     */
    public String getSnsToken() {
        return snsToken;
    }

    /**
     * SNS 토큰 : 미정
     * @param snsToken
     */
    public void setSnsToken(String snsToken) {
        this.snsToken = snsToken;
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

	/**
	 * 비밀번호 재설정 요청일시 - 기준 유효 기간 : 1시간
	 * @return
	 */
	public Date getPwdRequestDt() {
		return pwdRequestDt;
	}

	/**
	 * 비밀번호 재설정 요청일시 - 기준 유효 기간 : 1시간
	 * @param pwdRequestDt
	 */
	public void setPwdRequestDt(Date pwdRequestDt) {
		this.pwdRequestDt = pwdRequestDt;
	}

	/**
	 * 이용약관, 개인정보취급방침 동의 
	 * @return
	 */
	public String getUseAgreeYn() {
		return useAgreeYn;
	}

	/**
	 * 이용약관, 개인정보취급방침 동의
	 * @param useAgreeYn
	 */
	public void setUseAgreeYn(String useAgreeYn) {
		this.useAgreeYn = useAgreeYn;
	}

	/**
	 * 위치정보 약관 동의 - 선택
	 * @return
	 */
	public String getLocAgreeYn() {
		return locAgreeYn;
	}

	/**
	 * 위치정보 약관 동의 - 선택
	 * @param locAgreeYn
	 */
	public void setLocAgreeYn(String locAgreeYn) {
		this.locAgreeYn = locAgreeYn;
	}

	/**
	 * 정보 메일 수신 여부 - 선택
	 * @return
	 */
	public String getMailAgreeYn() {
		return mailAgreeYn;
	}

	/**
	 * 정보 메일 수신 여부 - 선택
	 * @param mailAgreeYn
	 */
	public void setMailAgreeYn(String mailAgreeYn) {
		this.mailAgreeYn = mailAgreeYn;
	}

	/**
	 * 차량 보유 여부
	 * @return
	 */
	public String getOwnYn() {
		return ownYn;
	}

	/**
	 * 차량 보유 여부
	 * @param ownYn
	 */
	public void setOwnYn(String ownYn) {
		this.ownYn = ownYn;
	}

	/**
	 * 제조사명
	 * @return
	 */
	public String getMakername() {
		return makername;
	}

	/**
	 * 제조사명
	 * @param makername
	 */
	public void setMakername(String makername) {
		this.makername = makername;
	}

	/**
	 * 모델명
	 * @return
	 */
	public String getCarname1name() {
		return carname1name;
	}

	/**
	 * 모델명
	 * @param carname1name
	 */
	public void setCarname1name(String carname1name) {
		this.carname1name = carname1name;
	}

	/**
	 * 세부모델명
	 * @return
	 */
	public String getCarname2name() {
		return carname2name;
	}

	/**
	 * 세부모델명
	 * @param carname2name
	 */
	public void setCarname2name(String carname2name) {
		this.carname2name = carname2name;
	}

	/**
	 * 연식 - yyyy
	 * @return
	 */
	public String getMakeYear() {
		return makeYear;
	}

	/**
	 * 연식 - yyyy
	 * @param makeYear
	 */
	public void setMakeYear(String makeYear) {
		this.makeYear = makeYear;
	}

	/**
	 * 주행거리
	 * @return
	 */
	public String getMileage() {
		return mileage;
	}

	/**
	 * 주행거리
	 * @param mileage
	 */
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}    
    
}