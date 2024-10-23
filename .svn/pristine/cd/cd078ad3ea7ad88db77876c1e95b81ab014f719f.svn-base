package com.golf.taglib;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.log4j.Logger;

/**
 * 커스텀 태그 라이브러리 - 옵션 리스트 태그
 * @author jynius
 *
 */
public class OptionListTag extends AbstractListTag implements DynamicAttributes {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = 4926481653151317662L;

	/**
	 * Logger Instance
	 */
	private Logger logger = Logger.getLogger(OptionListTag.class);

	private Object selected;

	private Collection<?> getCollectionSelected() {
		return CollectionUtil.getCollection(selected);
	}

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
	protected StringBuffer getHtml() {

		if (getList() == null || getListAsList().size() == 0) {
			return new StringBuffer();
		}

		StringBuffer sb = new StringBuffer();
		Object key;
		Collection<?> col = getCollectionSelected();
		for (int i = 0, s = getListAsList().size(); i < s; i++) {

			key = getKey(i);
			sb.append("<option value='").append(key).append("'");
			if (col != null && col.contains(key)) {
				sb.append(" selected");
			}
			sb.append(">").append(getValue(i)).append("</option>\n");
		}

		return sb;
	}

	public Object getSelected() {
		return selected;
	}

	public void setSelected(Object selected) {
		this.selected = selected;
	}
}
