package com.golf.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

@Deprecated
public class SortOrderTag extends TagSupport {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = 5083798320834010047L;

	/**
	 * log
	 */
	private static Logger log = Logger.getRootLogger();

	/**
	 * request parameter name of Sorting Field
	 */
	public static final String RPN_SORT_FIELD = "sortField";

	/**
	 * request parameter name of Sorting Order
	 */
	public static final String RPN_SORT_ORDER = "sortOrder";

	/**
	 * IMG tag for representing ascending order image source location:
	 * /common/images/icon/sort_not.gif
	 */
	public static final String TAG_IMG_SORT_NOT = "<IMG CLASS='sortnot' SRC='/iw-sec/common/images/icon/sort_not.gif' ALIGN='absmiddle'>";

	/**
	 * IMG tag for representing ascending order image source location:
	 * /common/images/icon/sort_asc.gif
	 */
	public static final String TAG_IMG_SORT_ASC = "<IMG CLASS='sortasc' SRC='/iw-sec/common/images/icon/sort_asc.gif' ALIGN='absmiddle'>";

	/**
	 * IMG tag for representing descending order image source location:
	 * /common/images/icon/sort_desc.gif
	 */
	public static final String TAG_IMG_SORT_DESC = "<IMG CLASS='sortdesc' SRC='/iw-sec/common/images/icon/sort_desc.gif' ALIGN='absmiddle'>";

	private String field = null;

	private String getSortField() {
		return pageContext.getRequest().getParameter(RPN_SORT_FIELD);
	}

	private String getSortOrder() {
		return pageContext.getRequest().getParameter(RPN_SORT_ORDER);
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public int doEndTag() throws JspException {

		String tagImage;
		if (!field.equals(getSortField())) {
			tagImage = TAG_IMG_SORT_NOT;
		} else if ("asc".equals(getSortOrder())) {
			tagImage = TAG_IMG_SORT_ASC;
		} else if ("desc".equals(getSortOrder())) {
			tagImage = TAG_IMG_SORT_DESC;
		} else {
			tagImage = TAG_IMG_SORT_NOT;
		}

		try {
			pageContext.getOut().print(tagImage);
		} catch (IOException e) {
			log.error("Write Error", e);
			throw new JspException(e);
		}

		return Tag.SKIP_BODY;
	}
}
