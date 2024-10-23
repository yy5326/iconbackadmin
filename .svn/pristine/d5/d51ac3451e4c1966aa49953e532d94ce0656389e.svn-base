package com.golf.taglib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author jynius
 *
 */
public class AbstractListTag extends AbstractTag {
	
	/**
	 * serial Version UID
	 */
	private static final long serialVersionUID = -2901962292583803399L;
	
	/**
	 * Logger Instance
	 */
	private Logger logger = Logger.getLogger(AbstractListTag.class);
	
	private Object list;
	private String listKey;
	private String listValue;
	private Method keyMethod;
	private Method valueMethod;
	
	private String indexKey;
	private String indexValue;
	
	protected List<?> getListAsList() {
		return (List<?>) list;
	}
	
	protected Object getItem(int i) {
		return getListAsList().get(i);
	}
	
	/**
	 * @param name
	 * @return
	 */
	private String getGetter(String name) {
		
		StringBuffer sb = new StringBuffer("get");
		sb.append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
		
		return sb.toString();
	}
	
	/**
	 * @return
	 */
	private Method getKeyMethod() {
		
//		if (keyMethod == null) {
//			
//			try {
//				keyMethod = getItem(0).getClass().getMethod(getGetter(getListKey()), (Class[]) null);
//			}
//			catch (SecurityException | NoSuchMethodException e) {
//				logger.error("get List Key Method['" + getListKey() + "'] Error: ", e);
//			}
//		}
		
		//주의 : 다른 형태의 객체가 바인딩될 수 있으므로 if null 제거
		
		try {
			keyMethod = getItem(0).getClass().getMethod(getGetter(getListKey()), (Class[]) null);
		}
		catch (SecurityException | NoSuchMethodException e) {
			logger.error("get List Key Method['" + getListKey() + "'] Error: ", e);
		}
		
		return keyMethod;
	}
	
	/**
	 * @return
	 */
	private Method getValueMethod() {
		
//		if (valueMethod == null) {
//			
//			try {
//				valueMethod = getItem(0).getClass().getMethod(getGetter(getListValue()), (Class[]) null);
//			}
//			catch (SecurityException | NoSuchMethodException e) {
//				logger.error("get List Value Method['" + getListValue() + "'] Error: ", e);
//			}
//		}
		//주의 : 다른 형태의 객체가 바인딩될 수 있으므로 if null 제거
		try {
			valueMethod = getItem(0).getClass().getMethod(getGetter(getListValue()), (Class[]) null);
		}
		catch (SecurityException | NoSuchMethodException e) {
			logger.error("get List Value Method['" + getListValue() + "'] Error: ", e);
		}
		
		return valueMethod;
	}
	
	protected Object getKey(int i) {
		
		if (listKey == null) {
			return getItem(i);
		}
		
		try {
			return getKeyMethod().invoke(getItem(i), (Object[]) null);
		}
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			logger.error("get " + i + "th Key['" + getItem(i) + "'] Value Error: ", e);
		}
		return null;
	}
	
	protected Object getValue(int i) {
		
		if (listValue == null) {
			return getItem(i);
		}
		
		try {
			return getValueMethod().invoke(getItem(i), (Object[]) null);
		}
		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			logger.error("get " + i + "th Value['" + getItem(i) + "'] Value Error: ", e);
		}
		
		return null;
	}
	
	public Object getList() {
		return list;
	}
	
	public void setList(Object object) {
		this.list = CollectionUtil.getList(object);
	}
	
	public String getListKey() {
		
		if (listKey == null) {
			listKey = "key";
		}
		
		return listKey;
	}
	
	public void setListKey(String listKey) {
		this.listKey = listKey;
	}
	
	public String getListValue() {
		
		if (listValue == null) {
			listValue = "value";
		}
		
		return listValue;
	}
	
	public void setListValue(String listValue) {
		this.listValue = listValue;
	}
	
	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}
	
	public String getIndexKey() {
		return indexKey;
	}
	
	public void setIndexValue(String indexValue) {
		this.indexValue = indexValue;
	}
	
	public String getIndexValue() {
		return indexValue;
	}
}
