package com.golf.model;

import com.golf.util.StringUtil;

/**
 * 공통 코드용 모델
 * @author wimy
 *
 */
@Deprecated
public class CommonCode implements GeneralModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2352065029584909516L;
	
	
	private String codeCd = "";
	private String codeNm = "";
	private String codeDesc = "";
	private int ordering = 0;
	
	public String getCodeCd() {
		return codeCd;
	}
	
	public void setCodeCd(String codeCd) {
		this.codeCd = codeCd;
	}
	
	/**
	 * [읽기전용] 코드 앞 4자리를 가져옵니다. 조건에 맞지 않을 경우 빈 값을 가져옵니다.
	 * @return
	 */
	public String getGroupCode() {
		if(!StringUtil.isEmpty(this.getCodeCd()) && this.getCodeCd().length() > 4)
			return this.getCodeCd().substring(0, 4);
		else
			return "";
	}
	
	/**
	 * [읽기전용] 코드 앞 4자리를 숫자로 가져옵니다. 조건에 맞지 않을 경우 -1을 가져옵니다.
	 * @return
	 */
	public int getGroupCodebyNumber() {
		if(!StringUtil.isEmpty(this.getGroupCode()))
			return Integer.parseInt(this.getGroupCode());
		else
			return -1;
	}
	
	public String getCodeNm() {
		return codeNm;
	}
	
	public void setCodeNm(String codeNm) {
		this.codeNm = codeNm;
	}
	
	public String getCodeDesc() {
		return codeDesc;
	}
	
	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}	
}
