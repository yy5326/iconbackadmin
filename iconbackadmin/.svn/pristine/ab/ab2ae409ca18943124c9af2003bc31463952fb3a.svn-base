package com.golf.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhncorp.lucy.security.xss.XssFilter;


/**
 * General Xss Filter filter : web.xml 참조
 * lucy-xss 필터 적용
 * */

public final class RequestWrapper extends HttpServletRequestWrapper {
	private static final Logger logger = LoggerFactory.getLogger(RequestWrapper.class);
	
	XssFilter filter = XssFilter.getInstance("lucy-xss-servlet-filter-rule.xml", true);
	
	public RequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameterMap = super.getParameterMap();
		Set<String> keySet = parameterMap.keySet();
		Iterator<String> itrator = keySet.iterator();
		Map<String, String[]> cleanMap = new HashMap<String, String[]>();
		while (itrator.hasNext()) {
			String key = itrator.next();
			String[] paramValues = parameterMap.get(key);
			if (paramValues == null) {
				cleanMap.put(key, paramValues);
			} else {
				int count = paramValues.length;
				String[] encodedValues = new String[count];
				for (int i = 0; i < count; i++) {
					//encodedValues[i] = EgovWebUtil.clearXSSMinimum(paramValues[i]);
					encodedValues[i] = filter.doFilter(paramValues[i]);
					
				}
				cleanMap.put(key, encodedValues);
			}
		}
		return cleanMap;
	}
	
	@Override
	public String[] getParameterValues(String parameter) {
		// logger.info("In getParameterValues .. parameter .......");
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			//encodedValues[i] = EgovWebUtil.clearXSSMinimum(values[i]);
			encodedValues[i] = filter.doFilter(values[i]);
		}
		return encodedValues;
	}
	
	@Override
	public String getParameter(String parameter) {
		// logger.info("In getParameter .. parameter .......");
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}
		// logger.debug("In getParameter RequestWrapper ........ value .......");
		//return EgovWebUtil.clearXSSMinimum(value);
		logger.debug("In getParameter RequestWrapper : {} : {} : {}", parameter, value, filter.doFilter(value));
		return filter.doFilter(value);
	}
	
	@Override
	public String getHeader(String name) {
		// logger.info("In getHeader .. parameter .......");
		String value = super.getHeader(name);
		if (value == null)
			return null;
		// logger.info("In getHeader RequestWrapper ........... value ....");
		//return EgovWebUtil.clearXSSMinimum(value);
		return filter.doFilter(value);
	}
	
}