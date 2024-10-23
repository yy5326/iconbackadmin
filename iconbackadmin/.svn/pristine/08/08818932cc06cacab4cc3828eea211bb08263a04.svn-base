package com.golf.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.log4j.Logger;

/**
 * 커스텀 태그 라이브러리 - 라디오 리스트 태그
 * @author jynius
 *
 */
public class RadioListTag extends AbstractListTag implements DynamicAttributes {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = 3854139146659788764L;

	/**
	 * Logger Instance
	 */
	private Logger logger = Logger.getLogger(RadioListTag.class);

	private String checked;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pulmuoneshop.taglib.CodeBaseTag#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {

		try {
			pageContext.getOut().print(getHtml());
		} catch (IOException e) {
			logger.error("page print Error: ", e);
		}

		return SKIP_BODY;
	}

	/**
	 * @return
	 */
	protected String getHtml() {

		if (getList() == null || getListAsList().size() == 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		Object key, value;
		for (int i = 0, s = getListAsList().size(); i < s; i++) {

			key = getKey(i);
			value = getValue(i);
			//클래스 추가 : 처음 엘리먼트 스킵
			String sAttr = getExtraAttributeString();
			if (i == 0 && getExtraAttributeString().contains("class="))
				sAttr = "";
			
			//sb.append("<span>");
			sb.append("<label><input type='radio' id='").append(getId())
					.append('.').append(key).append("'").append(" name='")
					.append(getName()).append("'").append(" value='")
					.append(key).append("'").append(" title='").append(value)
					.append("'").append(sAttr);
			if (getChecked() != null && getChecked().equals(key)) {
				sb.append(" checked");
			}
			sb.append("/> ").append(value).append("</label>");
			//sb.append("</span>");
			sb.append("\n");
		}

		return sb.toString();
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}
}
