package cn.com.prime.common.support;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import cn.com.prime.common.util.DateUtil;
import cn.com.prime.common.util.StringUtil;
/**
 * json对象辅助类
 * @author Rain
 *
 */
public class JsonHelper {
	/**
	 * 据说json==>对象是线程安全的
	 */
	public static ObjectMapper readMapper = new ObjectMapper();
	
	static{
		readMapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true) ;
		readMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		readMapper.setDateFormat(new SimpleDateFormat(DateUtil.defaultFormat));
	}
	
	public static <T> T parseToObject(InputStream is,Class<T> toClass){
		try {
			return (T)readMapper.readValue(is, toClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T parseToObject(byte [] b,int offset, int len, Class<T> valueType){
		try {
			return (T)readMapper.readValue(b,offset,len, valueType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T parseToObject(String json,Class<T> toClass){
		try {
			if(StringUtil.isNullOrEmpty(json)){
				return null;
			}
			return (T)readMapper.readValue(json, toClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static Map parseToMap(String json){
		return parseToObject(json,Map.class);
	}
	
	public static Map parseToMap(byte []b){
		if(b==null || b.length==0){
			return null;
		}
		return parseToObject(b,0,b.length,Map.class);
	}
	
	public static Map parseToMap(InputStream is){
		return parseToObject(is,Map.class);
	}
	
	public static Map parseToMap(Object o){
	    String oJson = parseToJson(o);
        return parseToObject(oJson,Map.class);
    }
	
	/**
	 * 将对象转化成Json
	 * @param o 对象
	 * @return
	 */
	public static String parseToJson(Object o){
		return parseToJson(o,false);
	}
	
	/**
	 * 将对象转化成Json
	 * @param o
	 * @param ignoreNull 是否忽略对象中为null的属性
	 * @return
	 */
	public static String parseToJson(Object o,boolean ignoreNull){
		if(o==null){
			return null;
		}
		ObjectMapper writerMapper = new ObjectMapper();
		writerMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
		writerMapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES,!ignoreNull);
		writerMapper.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES,!ignoreNull);
		writerMapper.setDateFormat(new SimpleDateFormat(DateUtil.defaultFormat));
		try {
			return writerMapper.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	

	
}
