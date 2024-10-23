package com.golf.model.common;

import org.apache.commons.lang.StringUtils;

import com.golf.model.GeneralModel;

/**
 * 
 * 사용자 모델 - 기본 모델<br>
 * 관리자 정보, 사용자 정보는 이 클래스를 상속 구현
 * 
 * @author wimy
 *
 */
public abstract class GeneralUserVO implements GeneralModel {

	private static final long serialVersionUID = -7775743905385517521L;

	String userID;

	String userName;

	String password;

	String passwordConfirm;

	MailAddressVO mailAddress;

	PhoneVO phoneNo;

	PhoneVO mobileNo;

	String checkedID;

	String continueStatus;

	/**
	 * 아이디 중복 체크 여부 : Y, N 문자열 사용
	 */
	public String getCheckedID() {
		if (StringUtils.isEmpty(this.checkedID))
			this.checkedID = "N";

		return this.checkedID;
	}

	/**
	 * 아이디 중복 체크 여부 : Y, N 문자열 사용
	 */
	public void setCheckedID(String checkedID) {
		this.checkedID = checkedID;
	}

	/**
	 * 로그인 상태 유지용 값
	 * 
	 * @return
	 */
	public String getContinueStatus() {
		return continueStatus;
	}

	/**
	 * 로그인 상태 유지용 값
	 * 
	 * @param continueStatus
	 */
	public void setContinueStatus(String continueStatus) {
		this.continueStatus = continueStatus;
	}

	/**
	 * 사용자 아이디
	 * 
	 * @return
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * 사용자 아이디
	 * 
	 * @param userID
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return this.passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public MailAddressVO getMailAddress() {
		if (this.mailAddress == null)
			this.mailAddress = new MailAddressVO();

		return this.mailAddress;
	}

	public void setMailAddress(MailAddressVO mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getEmail() {
		return this.getMailAddress().getAddress();
	}

	public void setEmail(String email) {
		this.getMailAddress().setAddress(email);

	}

	public PhoneVO getPhoneNo() {
		if (this.phoneNo == null)
			this.phoneNo = new PhoneVO();

		return this.phoneNo;
	}

	public void setPhoneNo(PhoneVO phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPhone() {
		return this.getPhoneNo().getNo();
	}

	public void setPhone(String phone) {
		this.getPhoneNo().setNo(phone);
	}

	public PhoneVO getMobileNo() {
		if (this.mobileNo == null)
			this.mobileNo = new PhoneVO();

		return this.mobileNo;
	}

	public void setMobileNo(PhoneVO mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getMobile() {
		return this.getMobileNo().getNo();
	}

	public void setMobile(String mobile) {
		this.getMobileNo().setNo(mobile);
	}

	// @Deprecated
	// public String getEncryptedPassword()
	// {
	// String sPwd = null;
	//
	// if(this.getPassword() != null) {
	// sPwd = Encrypt.SHA256Hash(this.getPassword());
	// }
	//
	// return sPwd;
	// }
}
