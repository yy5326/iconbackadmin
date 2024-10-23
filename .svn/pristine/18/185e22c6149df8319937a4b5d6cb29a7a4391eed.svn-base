package com.golf.taglib;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 커스텀 태그 라이브러리 - 말 줄임 태그
 * @author jynius
 *
 */
public class AbbrevTag extends TagSupport {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = 3226805934730041507L;

	private static final Log log = LogFactory.getLog(AbbrevTag.class);
	private static final Pattern REG_TAG = Pattern.compile("<([^>]+)>",
			Pattern.CASE_INSENSITIVE);
	private static final String ABBREV_NOTATION = "...";

	private enum TaggingType {
		HTML, ESCAPE, TEXT
	};

	private String value;
	private TaggingType type = TaggingType.HTML;
	private int length;
	private String notation;
	private boolean showTooltip = true;

	@Override
	public int doEndTag() throws JspException {

		try {
			pageContext.getOut().print(getAbbrev());
		} catch (IOException e) {
			log.error("pageContext getOut IOException: ", e);
		}

		return SKIP_BODY;
	}

	/**
	 * @return
	 */
	private String getAbbrev() {

		if (getValue() == null) {
			return null;
		}

		String value = getValue();
		switch (type) {
		case ESCAPE: {
			Matcher m = REG_TAG.matcher(value);
			StringBuffer sb = new StringBuffer();
			while (m.find() && sb.length() < length) {
				m.appendReplacement(sb, "&lt;" + m.group(1) + "&gt;");
			}

			if (sb.length() < length) {
				m.appendTail(sb);
			}

			value = sb.toString();
		}
			break;
		case TEXT: {
			Matcher m = REG_TAG.matcher(value);
			StringBuffer sb = new StringBuffer();
			while (m.find() && sb.length() < length) {
				m.appendReplacement(sb, "");
			}

			if (sb.length() < length) {
				m.appendTail(sb);
			}

			value = sb.toString();
		}
			break;
		case HTML:
		default:
			break;
		}

		if (value.length() > length) {
			value = new StringBuffer(value).replace(length, value.length(),
					getNotation()).toString();
		}
		
		//툴팁 표시
		if (isShowTooltip()) {
			value = new StringBuffer(value).insert(0, "<span title=\"" + getValue().replaceAll("\"", "&quot;") + "\">").append("</span>").toString();
		}
		
		return value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String strText) {
		this.value = strText;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type.toString();
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = TaggingType.valueOf(type);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getNotation() {

		if (notation == null) {
			notation = ABBREV_NOTATION;
		}

		return notation;
	}

	public void setNotation(String notation) {
		this.notation = notation;
	}

	public boolean isShowTooltip() {
		return showTooltip;
	}

	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}
	
	
}
