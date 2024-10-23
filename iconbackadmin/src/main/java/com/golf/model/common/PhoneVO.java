package com.golf.model.common;

import java.io.Serializable;

import com.golf.util.StringUtil;

/**
 * 전화번호 모델
 * 
 * @author wimy
 *
 */
public class PhoneVO implements Serializable {

	private static final long serialVersionUID = -3298463449590604490L;

	private String no;

	private String no1;
	private String no2;
	private String no3;
	private String no4;

	public String getNo() {

		if (!StringUtil.isEmpty(this.no1) && !StringUtil.isEmpty(this.no2) && !StringUtil.isEmpty(this.no3)) {
			StringBuffer sb = new StringBuffer(no1);
			sb.append('-').append(no2).append('-').append(no3);
			
			if (no4 != null)
				sb.append('-').append(no4);

			this.setNo(sb.toString());
		}
		return this.no;
	}

	public void setNo(String phone) {
		this.no = phone;
	}

	/**
	 * split no number
	 */
	private void splitPhone() {

		if (this.no == null) {
			return;
		}

		String[] s = null;

		if (this.no.indexOf("-") < 0) {
			if (this.no.length() > 10) {
				setNo1(this.no.substring(0, 3));
				setNo2(this.no.substring(3, 7));
				setNo3(this.no.substring(7, 11));
			} else {
				setNo1(this.no.substring(0, 3));
				setNo2(this.no.substring(3, 6));
				setNo3(this.no.substring(6, 10));
			}
		} else {
			s = this.no.split("-");

			if (s.length > 2) {
				setNo1(s[0]);
				setNo2(s[1]);
				setNo3(s[2]);
			}

			if (s.length > 3) {
				setNo4(s[3]);
			}
		}

	}

	/**
	 * @return no1
	 */
	public String getNo1() {

		if (no1 == null) {
			splitPhone();
		}

		return no1;
	}

	/**
	 * @param no1
	 */
	public void setNo1(String phone1) {
		this.no1 = phone1;
	}

	/**
	 * @return no2
	 */
	public String getNo2() {

		if (no2 == null) {
			splitPhone();
		}

		return no2;
	}

	/**
	 * @param no2
	 */
	public void setNo2(String phone2) {
		this.no2 = phone2;
	}

	/**
	 * @return no3
	 */
	public String getNo3() {

		if (no3 == null) {
			splitPhone();
		}

		return no3;
	}

	/**
	 * @param no3
	 */
	public void setNo3(String phone3) {
		this.no3 = phone3;
	}

	/**
	 * @return no4
	 */
	public String getNo4() {

		if (no4 == null) {
			splitPhone();
		}

		return no4;
	}

	/**
	 * @param no4
	 */
	public void setNo4(String phone4) {
		this.no4 = phone4;
	}

}
