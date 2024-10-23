package com.golf.taglib;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.log4j.Logger;

/**
 * 커스텀 태그 라이브러리 - 체크박스 리스트 태그
 * @author jynius
 *
 */
public class CheckboxListTag extends AbstractListTag implements
		DynamicAttributes {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = -1447162547964857237L;

	/**
	 * Logger Instance
	 */
	private Logger logger = Logger.getLogger(CheckboxListTag.class);

	private Object checked;

	private Collection<?> getCollectionChecked() {
		return CollectionUtil.getCollection(checked);
	}

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
		Collection<?> col = getCollectionChecked();
		for (int i = 0, s = getListAsList().size(); i < s; i++) {

			key = getKey(i);
			value = getValue(i);
			//클래스 추가 : 처음 엘리먼트 스킵
			String sAttr = getExtraAttributeString();
			if (i == 0 && getExtraAttributeString().contains("class="))
				sAttr = "";
			
			//sb.append("<span>");
			sb.append("<label><input type='checkbox' id='").append(getId())
					.append('.').append(key).append("'").append(" name='")
					.append(getName()).append("'").append(" value='")
					.append(key).append("'").append(" title='").append(value)
					.append("'").append(sAttr);
			if (col != null && col.contains(key)) {
				sb.append(" checked");
			}
			sb.append("/> ").append(value).append("</label>");
			//sb.append("</span>");
			sb.append("\n");
			
		}

		return sb.toString();
	}

	public Object getChecked() {
		return checked;
	}

	public void setChecked(Object checked) {
		this.checked = checked;
	}

}
