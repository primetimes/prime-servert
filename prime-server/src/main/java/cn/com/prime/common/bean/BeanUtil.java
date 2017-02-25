package cn.com.prime.common.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 2015/8/10 bean 相关的处理方法(反射处理)
 * @author Rain
 *
 */
public class BeanUtil {
	
	private static Map<String,Bean> beanCache = new HashMap<String,Bean>();
	
	/**
	 * 从缓存中获取Bean的详细
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	private static <T> Bean getBean(Class<T> clazz){
		if(beanCache.get(clazz.getName()) == null){
			Bean<T> bean = new Bean<T>();
			bean.setClazz(clazz);
			beanCache.put(clazz.getName(),bean);
		}
		return beanCache.get(clazz.getName());
	}
	
	public static void setDBPropertyValue(Object obj,String key,Object value){
		Bean<?> bean = getBean(obj.getClass());
		Map<String,Property> properties = bean.getDbProperites();
		Property p = properties.get(key);
		if(p!=null){
			p.setPropertyValue(obj, value);
		}
		
	}
	
	/**
	 * 从Map转成Bean
	 * @param valueMap
	 * @param clazz
	 * @return
	 */
	public static <T> T convertMapToBean(Map<String,Object> valueMap,Class<T> clazz){
		if(valueMap == null){
			return null;
		}
		T obj;
		try {
			obj = (T)clazz.newInstance();	
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		Bean<T> bean = getBean(clazz);
		Set<Entry<String, Object>> set = valueMap.entrySet();
		for(Entry<String, Object> e:set){
			String key = e.getKey();
			Object value = e.getValue();
			Property p = bean.getDBProperty(key);
			if(p!=null){
				p.setPropertyValue(obj, value);
			}
		}
		return (T)obj;
	}
	
	/**
	 * 从List<Map>转成List<BeanClass>
	 * @param list
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> convertMapToBeans(List<Map<String,Object>> list,Class<T> clazz){
		if(list == null){
			return null;
		}
		List<T> convertResult = new ArrayList<T>(list.size());
		for(Map m :list ){
			convertResult.add(convertMapToBean((Map<String,Object>)m, clazz));
		}
		return convertResult;
	}
	
	/**
	 * 将pojo类转成Map
	 * @param obj
	 * @return
	 */
	public static Map<String,Object> convertBeanToDBMap(Object obj){
		Map<String,Object> result = new HashMap<String,Object>();
		Bean<?> bean = getBean(obj.getClass());
		Map<String,Property> properties = bean.getDbProperites();
		Collection<Property> ps = properties.values();
		for(Property p:ps){
			result.put(p.getDbColumn(),p.getPropertyValue(obj));
		}
		return result;
	}
	
}
