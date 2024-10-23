package com.golf.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author jynius
 *
 */
public class AbstractTag extends TagSupport {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = -4652293283642708287L;

	private Map<String, String> attributes;

	private String name;
	private String className;

	/**
	 * @param uri
	 * @param name
	 * @param value
	 * @throws JspException
	 */
	public void setDynamicAttribute(String uri, String name, Object value)
			throws JspException {
		getAttributes().put(name, (String) value);
	}

	/**
	 * @return
	 */
	public Map<String, String> getAttributes() {

		if (attributes == null) {
			attributes = new HashMap<String, String>();
		}

		return attributes;
	}

	/**
	 * @param attributes
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return
	 */
	public String getExtraAttributeString() {

		if (attributes == null || attributes.size() == 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (String k : attributes.keySet()) {
			sb.append(" ").append(k).append('=').append('"')
					.append(attributes.get(k)).append('"');
		}

		return sb.toString();
	}

	@Override
	public String getId() {
		if (id == null) {
			id = getName();
		}
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

}
