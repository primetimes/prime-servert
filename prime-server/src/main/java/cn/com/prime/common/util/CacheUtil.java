package cn.com.prime.common.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.com.prime.common.bean.BeanUtil;

/**
 * 热数据缓存
 * 由于数据使用json格式存储，反序列化存在类型转换问题
 * @author Rain
 *
 */
public class CacheUtil {
	
	
	private static Integer redisDBID = 8;
	
	static{
		Integer configRedisDB = SystemMessage.getInteger("cache_redis_db");
		if(configRedisDB!=null){
			redisDBID = configRedisDB;
		}
	}
	
	/**
	 * 查询key失效时间，单位秒
	 * @param key
	 * @return
	 */
	public static long ttl(String key){
		return RedisFacade.ttl(redisDBID, key);
	}
	
	public static boolean exists(String key){
		return RedisFacade.exists(redisDBID, key);
	}
	
	public static <T> T get(String key,Class<T> clazz){
		
		String genericName = null;
		T obj = RedisFacade.get(redisDBID, key,clazz);
		if(List.class.isAssignableFrom(clazz)){ //如果是List
			genericName = RedisFacade.get(redisDBID,key+"_list_type");
		}
		if(StringUtil.isNotNullOrEmpty(genericName)){
			List<Map<String,Object>> list = (List)obj;
			try {
				return (T)BeanUtil.convertMapToBeans(list,Class.forName(genericName));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return obj;
	}
	
	
	public static void set(String key,Object obj,long milSeconds){
		RedisFacade.set(redisDBID, key, obj,milSeconds,TimeUnit.MILLISECONDS);
		if(obj == null){
			return;
		}
		if(obj instanceof List){ //如果是List
			List<Object> list = (List<Object>)obj;
			if((list.isEmpty())){
				return;
			}
			Object item = null;
			for(Object o:list){
				if(o!=null){
					item = o;
					break;
				}
			}
			//帜讯
			if(item!=null && item.getClass().getName().contains("cn.com.flaginfo")){
				RedisFacade.set(redisDBID, key+"_list_type", item.getClass().getName()
						,milSeconds*2,TimeUnit.MILLISECONDS);
			}
		}
	}
	
	public static void del(String key){
		RedisFacade.del(redisDBID, key);
	}
	
}
