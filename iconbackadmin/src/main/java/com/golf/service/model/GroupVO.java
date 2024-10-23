package com.golf.service.model;

import com.golf.model.common.SearchVO;

public class GroupVO extends SearchVO {

	private static final long serialVersionUID = -7832122992640138335L;

	private String accessToken;		//토큰
	private String userId;			//그룹웨어 ID
	private String empCode;			//사번
	private String psnName;			//사원명
	private String deptName;		//사원명
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	
	public String getPsnName() {
		return psnName;
	}

	public void setPsnName(String psnName) {
		this.psnName = psnName;
	}
	
	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
}
