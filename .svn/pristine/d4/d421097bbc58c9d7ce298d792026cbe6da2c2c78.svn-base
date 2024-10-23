package com.golf.taglib;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ContainsTag extends BodyTagSupport {

	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = -4605280436369931391L;

	private Log log = LogFactory.getLog(ContainsTag.class);

	private Collection<?> itemsCollection;
	private Iterable<String> testsIterable;
	private boolean notOperator = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {

		if (testsIterable == null) {
			return notOperator ? SKIP_BODY : EVAL_BODY_BUFFERED;
		}

		if (itemsCollection == null) {
			return notOperator ? EVAL_BODY_BUFFERED : SKIP_BODY;
		}

		boolean contains = false;
		for (Object o : testsIterable) {

			if (itemsCollection.contains(o)) {
				contains = true;
				break;
			}
		}

		return (!notOperator && contains) || (notOperator && !contains) ? EVAL_BODY_BUFFERED
				: SKIP_BODY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {

		try {
			if (bodyContent != null) {
				// bodyContent.writeOut(bodyContent.getEnclosingWriter());
				bodyContent.writeOut(pageContext.getOut());
				// pageContext.getOut().print(bodyContent.getString());
				bodyContent.clearBody();
			}
		} catch (IOException e) {
			log.error("IO Exception", e);
		}

		return EVAL_PAGE;
	}

	/**
	 * @param tests
	 *            the tests to set
	 */
	@SuppressWarnings("unchecked")
	public void setTests(Object tests) {
		testsIterable = (Iterable<String>) CollectionUtil.getList(tests);
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(Object items) {
		itemsCollection = CollectionUtil.getCollection(items);
	}

	public void setOperator(String operator) {
		notOperator = operator != null
				&& operator.length() > 0
				&& ("not".equals(operator.toLowerCase()) || "!"
						.equals(operator));
	}
}
