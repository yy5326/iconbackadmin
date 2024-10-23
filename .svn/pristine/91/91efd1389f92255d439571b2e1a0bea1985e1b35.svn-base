package com.golf.service.model;

import com.golf.model.GeneralModel;
import lombok.Data;
import com.golf.model.common.SearchVO;
import com.golf.model.common.PhoneVO;
import java.util.List;

//골프장 정보
public @Data class GolfInfVO extends SearchVO {

	private static final long serialVersionUID = 5835188151523792726L;

	private long golfSeq;
	private String golfName;
	private String golfTelnum;
	private String golfTelnum2;
	private String zipCode;
	private String city;
	private String golfAddr1;
	private String golfAddr2;
	private String islYn;
	private String picOffice1;
	private String picOffice2;
	private String picOffice3;
	private String picOfficeCode1;
	private String picOfficeCode2;
	private String picOfficeCode3;
	private String golfTypeCd;
	private double golfAddrLat;
	private double golfAddrLng;
	private String useYn;
	private String useYnNm;	
	private String target;
	private int fee1;
	private int fee2;
	private int fee3;
	private int fee4;
	private int fee5;
	private int fee6;
	private int fee7;
	private int fee8;
	private int fee9;
	private int fee10;
	private int fee11;
	private int fee12;
	
	// 리스트 체크박스
	private List<String> seqs;
    /*골프장 전화번호*/
	private PhoneVO golfTelNo;
	/*골프장 기타전화번호*/
	private PhoneVO golfTelNo2;
	
	/**
	 * 골프장 전화번호 변경
	 * @return
	 */
	public String getGolfTelnum() {
		return this.getGolfTelNo().getNo();
	}
	/**
	 * 골프장 전화번호 변경
	 * @param officePhone
	 */
	public void setGolfTelnum(String golfTelnum) {
		this.getGolfTelNo().setNo(golfTelnum);
	}
	
	/**
	 * 골프장 전화번호
	 * 
	 * @return
	 */
	public PhoneVO getGolfTelNo() {
		if (this.golfTelNo == null)
			this.golfTelNo = new PhoneVO();
		return golfTelNo;
	}
	/**
	 * 골프장 전화번호
	 * 
	 * @param 
	 */
	public void setGolfTelNo(PhoneVO golfTelNo) {
		this.golfTelNo = golfTelNo;
	}	

	/**
	 * 골프장 기타전화번호 변경
	 * @return
	 */
	public String getGolfTelnum2() {
		return this.getGolfTelNo2().getNo();
	}
	/**
	 * 골프장 기타전화번호 변경
	 * @param officePhone
	 */
	public void setGolfTelnum2(String golfTelnum2) {
		this.getGolfTelNo2().setNo(golfTelnum2);
	}
	
	/**
	 * 골프장 기타전화번호
	 * 
	 * @return
	 */
	public PhoneVO getGolfTelNo2() {
		if (this.golfTelNo2 == null)
			this.golfTelNo2 = new PhoneVO();
		return golfTelNo2;
	}
	/**
	 * 골프장 기타전화번호
	 * 
	 * @param 
	 */
	public void setGolfTelNo2(PhoneVO golfTelNo2) {
		this.golfTelNo2 = golfTelNo2;
	}	
}
