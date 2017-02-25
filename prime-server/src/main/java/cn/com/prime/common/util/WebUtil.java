package cn.com.prime.common.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web Util 类
 * 
 * @author Rain
 * 
 */
public class WebUtil {
	
	
	/**
	 * 从request获取所有的参数
	 * @param request
	 * @return
	 */
	public static Map<String, Object> bindParamToMap(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration enumer = request.getParameterNames();
		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			map.put(key, request.getParameter(key));
		}
		return map;
	}
	
	public static Double stringToDouble(String str) {
		try {
			return Double.valueOf(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static Double getDouble(String str) {
		return stringToDouble(str);
	}

	public static String getString(Double db) {
		if (db == null)
			return "";
		return db.toString();
	}

	private static String getString(String str) {
		if (str == null || str.equals("null"))
			return "";
		return str;
	}

	public static Integer getInteger(String str) {
		if (str == null || str.equals("null") || "".equals(str))
			return null;
		return new Integer(str);
	}

	public static int getInt(String str) {
		if (str == null || str.equals("null"))
			return 0;
		return new Integer(str).intValue();
	}

	public static String getParamByRegex(HttpServletRequest request,
			String regex) {

		Enumeration en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();
			if (name.matches(regex)) {
				return request.getParameter(name);
			}
		}
		return null;
	}

	/**
	 * 将参数放到request中
	 * 
	 * @param request
	 */
	public static void setParams(HttpServletRequest request) {
		Enumeration en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();
			Object value = (Object) request.getParameter(name);
			System.out.println("name=" + name + ";;value=" + value);
			request.setAttribute(name, value);
		}
	}

	/**
	 * ֵ
	 * 
	 * @param request
	 * @param paramName
	 * @param def
	 * @return
	 */
	public static int getInt(HttpServletRequest request, String paramName,
			int def) {
		String str = getString(request.getParameter(paramName));
		if (str.equals("")) {
			return def;
		} else {
			return new Integer(str).intValue();
		}
	}

	public static Integer getInteger(HttpServletRequest request,
			String paramName) {
		String str = getString(request.getParameter(paramName));
		return getInteger(str);
	}

	public static String getString(HttpServletRequest request, String paramName) {
		return getString(request.getParameter(paramName));
	}

	public static String[] getStrings(HttpServletRequest request,
			String paramName) {
		return request.getParameterValues(paramName);
	}

	public static String getSelected(String value1, String value2) {
		if (value1 == null || value2 == null)
			return "";
		if (value1.equals(value2))
			return "selected";
		return "";
	}

	/**
	 * 获取访问IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIPAddress(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

	/**
	 * 设置Session
	 * 
	 * @param request
	 * @param SessionName
	 * @param value
	 * @param maxAge
	 */
	public static void addSession(HttpServletRequest request,
			String SessionName, String value, int maxAge) {
		request.getSession().setAttribute(SessionName, value);
		// request.getSession().setMaxInactiveInterval(maxAge);
	}

	/**
	 * 设置cookie
	 * 
	 * @param response
	 * @param cookieName
	 * @param value
	 * @param maxAge
	 */
	public static void addCookie(HttpServletResponse response,
			String cookieName, String value, int maxAge) {
		Cookie cookie = new Cookie(cookieName, value);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}
	

	/**
	 * 设置cookie 默认为一年
	 * 
	 * @param response
	 * @param cookieName
	 * @param value
	 */
	public static void addCookie(HttpServletResponse response,
			String cookieName, String value) {
		addCookie(response, cookieName, value, 365 * 24 * 60 * 60);
	}
	
	public static void removeCookie(HttpServletRequest request,HttpServletResponse response,
			String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	/**
	 * 获取cookie的值
	 * 
	 * @param response
	 * @param cookieName
	 * @param value
	 */
	public static String getCookieValue(HttpServletRequest request,
			String cookieName) {
		Cookie cookie = getCookie(request, cookieName);
		if (cookie == null) {
			return null;
		}
		return cookie.getValue();
	}
	
	

	/**
	 * 返回Cookie
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie ck[] = request.getCookies();
		if (ck == null)
			return null;
		for (Cookie c : ck) {
			if (c.getName().equals(cookieName)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * 数组元素toString，以逗号分隔串联起来
	 * 
	 * @param items
	 *            数组
	 * @return 数组字符串表示
	 */
	public static String arrayToString(Object[] items) {
		if (items == null)
			return null;
		String result = "";
		for (int i = 0; i < items.length; i++) {
			result += items[i].toString();
			if (i < items.length - 1)
				result += ",";
		}
		return result;
	}

}
