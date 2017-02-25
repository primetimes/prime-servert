package cn.com.prime.common.support;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 常量定义
 * 名称要形象
 * @author Rain
 *
 */
public class SystemConstant {
	
	public static final String YES = "1";
	
	public static final String NO = "0";
	
	//锁键后缀
	public static final String LOCK_KEY_SUFFIX = "_LOCK";
	
	/**
	 * 返回成功"200"
	 */ 
	public static final String RETURN_SUCC = "200";
	
	/**
	 * 返回码key
	 */
	public static final String KEY_RETURN_CODE = "returnCode";

	/**
	 * 服务调用返回错误码字段key值
	 */
	public static final String KEY_SERVICE_ERROR_CODE = "errorBizCode";
	public static final String KEY_SERVICE_ERROR_MSG = "errorBizMsg";
	
	/**
	 * 文件类型错误 904
	 */
	public static String ERROR_CODE_FILE_FORMAT = "904";
	
	public static Map<String, Object> toMap() {
		Field fs[] = SystemConstant.class.getFields();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field f : fs) {
			String key = f.getName();
			try {
				Object o = f.get(key);
				map.put(key, o);
			} catch (Exception e) {
				e.printStackTrace();
				// throw new DataException(e);
			}

		}

		return map;
	}

}
