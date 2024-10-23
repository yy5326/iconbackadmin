package com.golf.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Class Name : RequestUtils.java Description : Request 를 통한 parameter 전달
 *
 * Modification Information Mod Date Modifier Description -------- --------
 * --------------------------- 2010.12.07. 이진홍 최초 생성
 *
 */
public class RequestUtils {
	private static final Logger logger = Logger.getLogger(RequestUtils.class);

	/**
	 * Get string(if object is null, return empty string).
	 * 
	 * @param Object
	 * @return String
	 */
	public static String getString(Object object) {
		if (object == null)
			return "";
		else
			return (String) object;
	}

	/**
	 * Convert request parameter to map.
	 * 
	 * @param HttpServletRequest
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getMap(HttpServletRequest request)
			throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		Enumeration enums = request.getParameterNames();

		while (enums.hasMoreElements()) {

			String name = (String) enums.nextElement();
			String[] values = request.getParameterValues(name);

			if (values != null) {
				if (values.length == 1) {
					param.put(name, getString(values[0]));
					logger.info("■■■ " + name + " = " + values[0]);
				} else if (values.length > 1) {
					for (int i = 0; i < values.length; i++) {
						values[i] = getString(values[i]);
						logger.info("■■■ " + name + " " + i + " = " + values[i]);
					}
					param.put(name, values);
				} else if (values.length < 1) {
					param.put(name, "");
					logger.info("■■■ " + name + " = ");
				}
			} else {
				param.put(name, "");
				logger.info("■■■ " + name + " = ");
			}
		}

		return param;
	}

	/**
	 * Convert request parameter to bean.
	 * 
	 * @param HttpServletRequest
	 * @param Class
	 * @return Object
	 */
	@SuppressWarnings("rawtypes")
	public static Object getBean(HttpServletRequest request, Class beanClass)
			throws Exception {
		Map<String, Object> properties = getMap(request);
		Object bean = beanClass.newInstance();
		BeanUtils.populate(bean, properties);

		return bean;
	}

	@SuppressWarnings("rawtypes")
	public static Object getBean(Class beanClass) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		Map<String, Object> properties = getMap(request);
		Object bean = beanClass.newInstance();
		BeanUtils.populate(bean, properties);

		return bean;
	}

	@SuppressWarnings("rawtypes")
	public static Object getBean(HttpServletRequest request, Class beanClass,
			boolean bt) throws Exception {
		Map<String, Object> properties = getMap(request);
		Object bean = beanClass.newInstance();
		BeanUtils.populate(bean, properties);

		return bean;
	}

	@SuppressWarnings("rawtypes")
	public static Object getBean(MultipartHttpServletRequest request,
			Class beanClass) throws Exception {
		Map<String, Object> properties = getMap(request);
		Object bean = beanClass.newInstance();
		BeanUtils.populate(bean, properties);

		return bean;
	}

	/**
	 * Convert request parameter to int.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return int
	 */
	public static int getInt(HttpServletRequest request, String name)
			throws Exception {
		return getInt(request, name, 0);
	}

	/**
	 * Convert request parameter to int with default value.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return int
	 */
	public static int getInt(HttpServletRequest request, String name,
			int defaultValue) throws Exception {
		String value = getString(request, name);
		// logger.info("##### " + name.toUpperCase() + " : " + value);
		try {
			defaultValue = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}

		return defaultValue;
	}

	/**
	 * Convert request parameter to long.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return long
	 */
	public static long getLong(HttpServletRequest request, String name)
			throws Exception {
		return getLong(request, name, 0);
	}

	/**
	 * Convert request parameter to long width default value.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return long
	 */
	public static long getLong(HttpServletRequest request, String name,
			long defaultValue) throws Exception {

		String value = getString(request, name);
		// logger.info("##### " + name.toUpperCase() + " : " + value);
		try {
			defaultValue = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}

		return defaultValue;
	}

	/**
	 * Convert request parameter to double.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return double
	 */
	public static double getDouble(HttpServletRequest request, String name)
			throws Exception {
		return getDouble(request, name, 0);
	}

	/**
	 * Convert request parameter to double with default value.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return double
	 */
	public static double getDouble(HttpServletRequest request, String name,
			double defaultValue) throws Exception {

		String value = getString(request, name);
		// logger.info("##### " + name.toUpperCase() + " : " + value);
		try {
			defaultValue = Double.parseDouble(value);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}

		return defaultValue;
	}

	/**
	 * Convert request parameter to float.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return float
	 */
	public static float getFloat(HttpServletRequest request, String name)
			throws Exception {
		return getFloat(request, name, 0);
	}

	/**
	 * Convert request parameter to float with default value.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return float
	 */
	public static float getFloat(HttpServletRequest request, String name,
			float defaultValue) throws Exception {

		String value = getString(request, name);
		// logger.info("##### " + name.toUpperCase() + " : " + value);
		try {
			defaultValue = Float.parseFloat(value);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}

		return defaultValue;
	}

	/**
	 * Convert request parameter to String.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return String
	 */
	public static String getString(HttpServletRequest request, String name)
			throws Exception {
		return getString(request, name, "");
	}

	/**
	 * Convert request parameter to String with default value.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return String
	 */
	public static String getString(HttpServletRequest request, String name,
			String defaultValue) throws Exception {
		String value = request.getParameter(name);
		// logger.info("##### " + name.toUpperCase() + " : " + value);
		if (value == null)
			return defaultValue;
		else
			return value.trim();
	}

	/**
	 * Convert request parameter to String with default value.
	 * 
	 * @param HttpServletRequest
	 * @param name
	 * @return String
	 */
	public static String getArrayToString(HttpServletRequest request,
			String name) throws Exception {
		String[] value = request.getParameterValues(name);
		String retVal = "";

		if (value != null) {
			for (int i = 0; i < value.length; i++) {
				retVal = retVal + value[i].trim() + ",";
			}
			if (retVal.length() > 0)
				retVal = retVal.substring(0, retVal.length() - 1);
		}
		// logger.info("##### " + name.toUpperCase() + " : " + retVal);
		return retVal.trim();
	}

	/**
	 * @method Name : getArrayParameter
	 * @Description : request 객체를 통해 받은 parameter의 값 전체를 ArrayList 형태로 반환한다.
	 * @param : request(HttpServletRequest) : servlet의 request 객체 name(String):
	 *        반환받고자 하는 parameter 이름
	 * @return : ArrayList
	 */
	public static ArrayList<String> getArrayParameter(
			HttpServletRequest request, String name) throws Exception {

		ArrayList<String> param = new ArrayList<String>();

		String[] values = request.getParameterValues(name);

		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				param.add(i, values[i]);
			}
		} else {
			param.add("");
		}

		return param;
	}

	public static String getURL(HttpServletRequest request) {
		Enumeration<?> param = request.getParameterNames();

		StringBuffer strParam = new StringBuffer();
		StringBuffer strURL = new StringBuffer();

		if (param.hasMoreElements()) {
			strParam.append("?");
		}

		while (param.hasMoreElements()) {
			String name = (String) param.nextElement();
			String value = request.getParameter(name);

			strParam.append(name + "=" + value);

			if (param.hasMoreElements()) {
				strParam.append("&");
			}
		}

		strURL.append(request.getRequestURI());
		strURL.append(strParam);

		return strURL.toString();
	}
}
