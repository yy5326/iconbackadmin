package com.golf.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.log4j.Logger;

/**
 * 커스텀 태그 라이브러리 - 셀렉트 태그
 * @author jynius
 *
 */
public class SelectTag extends OptionListTag implements DynamicAttributes {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = -5189244375594941356L;

	/**
	 * Logger Instance
	 */
	private Logger logger = Logger.getLogger(SelectTag.class);

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
	@Override
	protected StringBuffer getHtml() {

		StringBuffer sb = new StringBuffer().append("<select id='")
				.append(getId()).append("'").append(" name='")
				.append(getName()).append("'")
				.append(getExtraAttributeString()).append(">\n");

		// 셀렉트 박스 처음 option 객체
		if (getIndexKey() != null && getIndexValue() != null) {
			// sb.append("<option value=\"" + getIndexKey() + "\" " +
			// (getSelected().equals(getIndexKey()) ? "selected=\"selected\"" :
			// "") + ">" + getIndexValue() + "</option>\n");
			sb.append("<option value=\"" + getIndexKey() + "\">"
					+ getIndexValue() + "</option>\n");
		}

		sb.append(super.getHtml()).append("</select>\n");

		return sb;
	}
}
