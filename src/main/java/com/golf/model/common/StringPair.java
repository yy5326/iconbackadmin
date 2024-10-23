package com.golf.model.common;

import java.io.Serializable;

/**
 * 스트링페어 - text, value 쌍 객체 <br>
 * 셀렉트 박스 따위의 키, 값 쌍 객체 바인딩용
 * 
 * @author wimy
 *
 */
public class StringPair implements Serializable {

	private static final long serialVersionUID = -3414172043986630344L;
	private String text;
	private String value;

	/**
	 * 노 옵션 생성 빈 문자열 텍스트, 값 생성
	 */
	public StringPair() {
		new StringPair("", "");
	}

	/**
	 *
	 * @param text
	 *            : 텍스트
	 * @param value
	 *            : 값
	 */
	public StringPair(String text, String value) {
		setText(text);
		setValue(value);
	}

	/**
	 * @return the key
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
