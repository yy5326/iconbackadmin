package com.golf.taglib;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Struts2 IterableUtil 참조
 * </p>
 * 
 * @author jynius
 */
public class CollectionUtil {

	/**
	 * Determine whether a given object can be made into an
	 * <code>Iterator</code>
	 *
	 * @param object
	 *            the object to check
	 * @return <code>true</code> if the object can be converted to an iterator
	 *         and <code>false</code> otherwise
	 */
	public static boolean isCollectable(Object object) {

		if (object == null) {
			return false;
		}

		if (object instanceof Collection) {
			return true;
		}

		if (isListable(object)) {
			return true;
		}

		if (object instanceof Map) {
			return true;
		}

		else {
			return false;
		}
	}

	/**
	 * Determine whether a given object can be made into an <code>List</code>
	 *
	 * @param object
	 *            the object to check
	 * @return <code>true</code> if the object can be converted to an List and
	 *         <code>false</code> otherwise
	 */
	public static boolean isListable(Object object) {

		if (object == null) {
			return false;
		} else if (object instanceof List) {
			return true;
		} else if (object.getClass().isArray()) {
			return true;
		} else if (object instanceof Enumeration) {
			return true;
		} else if (object instanceof String) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<?> getCollection(Object object) {

		if (object == null) {
			return null;
		}

		if (object instanceof Collection) {
			return (Collection<?>) object;
		}

		if (isListable(object)) {
			return getList(object);
		}

		if (object instanceof Map) {
			return ((Map) object).entrySet();
		}

		// if(object instanceof String) {
		// ArrayList<StringPair> list = new ArrayList<StringPair>();
		// list.add(new StringPair(String.valueOf(object),
		// String.valueOf(object)));
		// return list;
		// }

		return Arrays.asList(object);
	}

	/**
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<?> getList(Object object) {

		if (object == null) {
			return null;
		}

		if (object instanceof List) {
			return (List<?>) object;
		}

		if (object.getClass().isArray()) {

			// need ability to support primitives; therefore, cannot
			// use Object[] casting.
			List list = new ArrayList(Array.getLength(object));

			for (int j = 0; j < Array.getLength(object); j++) {
				list.add(Array.get(object, j));
			}

			return list;
		}

		if (object instanceof Enumeration) {

			Enumeration enumeration = (Enumeration) object;
			List list = new ArrayList();

			while (enumeration.hasMoreElements()) {
				list.add(enumeration.nextElement());
			}

			return list;
		}

		if (object instanceof String) {
			return Arrays.asList(((String) object));
		}

		return null;
	}

	/**
	 * @param map
	 * @param isKeySet
	 * @return
	 */
	public static Iterator<String> getIterator(Map<String, String> map,
			boolean isKeySet) {

		if (map == null) {
			return null;
		}

		if (isKeySet) {
			return map.keySet().iterator();
		} else {
			List<String> list = new ArrayList<String>();
			for (String key : map.keySet()) {
				list.add(map.get(key));
			}

			return list.iterator();
		}
	}
}
