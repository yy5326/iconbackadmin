package com.golf.model.common;

import com.golf.model.GeneralModel;

/**
 * 공통 alert 모델 <br>
 * /jsp/common/alertPage.jsp 참조
 * 
 * @author wimy
 *
 */
public class AlertModel implements GeneralModel {

	private static final long serialVersionUID = -7632599158911806347L;

	private String message = "";
	private String href = "";
	private String script = "";
	private boolean layered = false;
	private boolean isMobile = false;

	public AlertModel() {
	}

	/**
	 * 공통 alert page용 모델 사용하지 않을 파라미터는 null을 넣어야 합니다.
	 * 
	 * @param alertMessage
	 *            alert 문구
	 * @param href
	 *            location.href 경로
	 * @param scriptString
	 *            실행 스크립트 문자열
	 */
	public AlertModel(String alertMessage, String href, String scriptString) {
		if (alertMessage != null)
			this.message = alertMessage;

		if (href != null)
			this.href = href;

		if (scriptString != null)
			this.script = scriptString;
	}

	/**
	 * 공통 alert page용 모델 alert message와 실행할 스크립트 문자열을 설정할 수 있습니다.
	 * 
	 * @param alertMessage
	 * @param scriptString
	 */
	public AlertModel(String alertMessage, String scriptString) {
		this(alertMessage, null, scriptString);
	}

	/**
	 * alert 메시지
	 * .replaceAll("\r\n", " ") 추가
	 * .replaceAll("\n", "\\n") 추가
	 * @return
	 */
	public String getMessage() {
		return this.message.replaceAll("\r\n", " ").replaceAll("\n", "\\n");
	}

	/**
	 * alert 메시지
	 * 
	 * @return
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * location.href 문자열
	 * 
	 * @return
	 */
	public String getHref() {
		return href;
	}

	/**
	 * location.href 문자열
	 * 
	 * @return
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * 스크립트 실행 문자열
	 * 
	 * @return
	 */
	public String getScript() {
		return script;
	}

	/**
	 * 스크립트 실행 문자열
	 * 
	 * @return
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * 레이어로 표시여부 (false는 alert)
	 * @return
	 */
	public boolean isLayered() {
		return layered;
	}

	/**
	 * 레이어로 표시여부 (false는 alert)
	 * @param layered
	 */
	public void setLayered(boolean layered) {
		this.layered = layered;
	}

	/**
	 * 모바일 접근 여부 (meta 추가 필요함)
	 * @return
	 */
	public boolean isMobile() {
		return isMobile;
	}

	/**
	 * 모바일 접근 여부 (meta 추가 필요함)
	 * @param isMobile
	 */
	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}

	
	
}
