package cn.com.prime.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.prime.common.support.JsonHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

/**
 * Redis操作简单封装，采用门面模式
 * 
 * @author ming.tan@flaginfo.com.cn
 * @date 2014年9月11日 下午1:36:14
 */
public class RedisFacade {

	private static final Logger log = LoggerFactory.getLogger(RedisFacade.class);

	/**
	 * Redis池映射hash表
	 */
	private static final ConcurrentHashMap<Integer, JedisPool> REDIS_POOL_MAP = new ConcurrentHashMap<Integer, JedisPool>();

	/**
	 * redis默认每次最大匹配大小为10000
	 */
	private static final int DEFAULT_MATCH_SIZE = 10000;

	private static final int DEFAULT_REDIS_DATABASE = 3;

	static {
		initPool(DEFAULT_REDIS_DATABASE);
	}

	private static void initPool(int database) {
		String ip = SystemMessage.getString("redis_conf_master_ip");
		int port = SystemMessage.getInteger("redis_conf_master_port");

		JedisPoolConfig config = new JedisPoolConfig();
		Integer maxTotal = SystemMessage.getInteger("redis_conf_maxTotal");
		Integer maxIdle = SystemMessage.getInteger("redis_conf_maxIdle");
		Integer maxWaitMills = SystemMessage.getInteger("redis_conf_maxWaitMillis");
		Integer poolTimeout = SystemMessage.getInteger("redis_conf_poolTimeout");
		Boolean testOnBorrow = SystemMessage.getBoolean("redis_conf_testOnBorrow");
		Boolean testOnReturn = SystemMessage.getBoolean("redis_conf_testOnReturn");

		String password = SystemMessage.getString("redis_conf_password");
		if (StringUtil.isNullOrEmpty(password))
			password = null;

		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWaitMills);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);

		JedisPool pool = null;
		try {
			pool = new JedisPool(config, ip, port, poolTimeout, password, database);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (pool != null)
			REDIS_POOL_MAP.putIfAbsent(database, pool);
	}

	/**
	 * 根据redis数据库索引获取jedis池
	 * @param database
	 * @return
	 */
	private static JedisPool getPool(int database) {
		JedisPool pool = REDIS_POOL_MAP.get(database);
		while (pool == null) {
			initPool(database);
			pool = REDIS_POOL_MAP.get(database);
		}
		return pool;
	}

	/**
	 * 获取Redis实例.
	 * 
	 * @return Redis工具类实例
	 */
	public static Jedis getJedis() {
		return getJedis(DEFAULT_REDIS_DATABASE);
	}

	/**
	 * 获取Jedis
	 * @param database
	 * @return
	 */
	public static Jedis getJedis(int database) {
		Jedis jedis = null;
		JedisPool pool = null;
		int count = 0;
		do {
			try {
				pool = getPool(database);
				jedis = pool.getResource();
			} catch (Exception e) {
				log.error("get redis master1 failed!", e);
				// 销毁对象
				pool.returnBrokenResource(jedis);
			}
			count++;
		} while (jedis == null && count < 3);
		return jedis;
	}

	/**
	 * 释放redis实例到连接池.
	 * 
	 * @param jedis
	 *            redis实例
	 */
	public static void closeJedis(Jedis jedis) {
		closeJedis(DEFAULT_REDIS_DATABASE, jedis);
	}

	/**
	 * 释放redis实例到连接池.
	 * 
	 * @param jedis
	 *            redis实例
	 */
	public static void closeJedis(int database, Jedis jedis) {
		if (jedis != null) {
			JedisPool pool = getPool(database);
			pool.returnResource(jedis);
		}
	}

	/**
	 * 判断key在redis中是否存在
	 * 
	 * @param key
	 * @return
	 */
	public static boolean exists(String key) {
		return exists(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 判断key在redis中是否存在
	 * 
	 * @param key
	 * @return
	 */
	public static boolean exists(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			boolean result = jedis.exists(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 根据key从redis中获取值
	 * 
	 * @param key
	 */
	public static <T> T get(String key, Class<T> clazz) {
		return get(DEFAULT_REDIS_DATABASE, key, clazz);
	}

	/**
	 * 根据key从redis中获取值
	 * 
	 * @param key
	 */
	public static <T> T get(int database, String key, Class<T> clazz) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		String json = get(database, key);
		T result = JsonHelper.parseToObject(json, clazz);
		return result;

	}

	/**
	 * 根据key从redis中获取字符串值
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return get(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 根据key从redis中获取字符串值
	 * 
	 * @param key
	 * @return
	 */
	public static String get(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String value = jedis.get(key);
			return value;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 从redis中批量获取字符串值
	 * 
	 * @param keys
	 * @return
	 */
	public static List<String> mget(int database, String... keys) {
		if (keys == null)
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			List<String> values = jedis.mget(keys);
			return values;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 从redis中批量获取字符串值
	 * 
	 * @param keys
	 * @return
	 */
	public static List<String> mget(String... keys) {
		return mget(DEFAULT_REDIS_DATABASE, keys);
	}

	/**
	 * 保存字符串到redis中，永不过期
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean set(int database, String key, String value) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.set(key, value);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 保存字符串到redis中，永不过期
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public static boolean set(String key, String value) {
		return set(DEFAULT_REDIS_DATABASE, key, value);
	}

	/**
	 * 保存对象到redis中，永不过期
	 * 
	 * @param key
	 * @param obj
	 * @return
	 */
	public static boolean set(int database, String key, Object obj) {
		String value = JsonHelper.parseToJson(obj);
		return set(database, key, value);
	}

	/**
	 * 保存对象到redis中，永不过期
	 * 
	 * @param key
	 * @param obj
	 * @return
	 */
	public static boolean set(String key, Object obj) {
		return set(DEFAULT_REDIS_DATABASE, key, obj);
	}

	/**
	 * 设置key的值和超时时间
	 * 
	 * @param key
	 * @param value
	 * @param time
	 * @param unit
	 * @return
	 */
	public static boolean set(int database, String key, String value, long time, TimeUnit unit) {
		int seconds = (int) unit.toSeconds(time);
		return setex(database, key, value, seconds);
	}

	/**
	 * 设置key的值和超时时间
	 * 
	 * @param key
	 * @param value
	 * @param time
	 * @param unit
	 * @return
	 */
	public static boolean set(String key, String value, long time, TimeUnit unit) {
		return set(DEFAULT_REDIS_DATABASE, key, value, time, unit);
	}

	/**
	 * 设置key的值和超时时间
	 * 
	 * @param key
	 * @param obj
	 * @param time
	 * @param unit
	 * @return
	 */
	public static boolean set(int database, String key, Object obj, long time, TimeUnit unit) {
		String value = JsonHelper.parseToJson(obj);
		return set(database, key, value, time, unit);
	}

	/**
	 * 设置key的值和超时时间
	 * 
	 * @param key
	 * @param obj
	 * @param time
	 * @param unit
	 * @return
	 */
	public static boolean set(String key, Object obj, long time, TimeUnit unit) {
		return set(DEFAULT_REDIS_DATABASE, key, obj, time, unit);
	}
	
	public static boolean setnx(String key,String value) {
		return setnx(DEFAULT_REDIS_DATABASE, key, value);
	}
	
	public static boolean setnx(int database, String key,String value) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			long b = jedis.setnx(key, value);
			if(b==0){
				return false;
			}else{
				return true;
			}
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 设置key的值为value,并设置过期时间为指定的seconds秒
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public static boolean setex(int database, String key, String value, int seconds) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.setex(key, seconds, value);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 设置key的值为value,并设置过期时间为指定的seconds秒
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public static boolean setex(String key, String value, int seconds) {
		return setex(DEFAULT_REDIS_DATABASE, key, value, seconds);
	}

	/**
	 * 设置key的值为value,并设置过期时间为指定的seconds秒
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public static boolean setex(int database, String key, Object obj, int seconds) {
		String value = JsonHelper.parseToJson(obj);
		return setex(database, key, value, seconds);
	}

	/**
	 * 设置key的值为value,并设置过期时间为指定的seconds秒
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public static boolean setex(String key, Object obj, int seconds) {
		return setex(DEFAULT_REDIS_DATABASE, key, obj, seconds);
	}

	/**
	 * 返回key对应的值，并设置key对应的新值为value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String getSet(int database, String key, String value) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String oldValue = jedis.getSet(key, value);
			return oldValue;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 返回key对应的值，并设置key对应的新值为value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String getSet(String key, String value) {
		return getSet(DEFAULT_REDIS_DATABASE, key, value);
	}

	/**
	 * 返回key对应的值，并设置key对应的新值为value
	 * 
	 * @param key
	 * @param value
	 * @param clazz
	 * @return
	 */
	public static <T> T getSet(int database, String key, Object value, Class<T> clazz) {
		String jsonValue = JsonHelper.parseToJson(value);
		String oldValue = getSet(database, key, jsonValue);
		return JsonHelper.parseToObject(oldValue, clazz);
	}

	/**
	 * 返回key对应的值，并设置key对应的新值为value
	 * 
	 * @param key
	 * @param value
	 * @param clazz
	 * @return
	 */
	public static <T> T getSet(String key, Object value, Class<T> clazz) {
		return getSet(DEFAULT_REDIS_DATABASE, key, value, clazz);
	}

	/**
	 * 批量设置key 和 value
	 * 
	 * @param keysvalues
	 *            key1 value1 key2 value ....
	 * @return
	 */
	public static boolean mset(int database, String... keysvalues) {
		if (keysvalues == null)
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.mset(keysvalues);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 批量设置key 和 value
	 * 
	 * @param keysvalues
	 *            key1 value1 key2 value ....
	 * @return
	 */
	public static boolean mset(String... keysvalues) {
		return mset(DEFAULT_REDIS_DATABASE, keysvalues);
	}

	/**
	 * 在key对应的值后面追加字符串，如果key不存在，则相当于set(key,value)
	 * 
	 * @param key
	 * @param value
	 */
	public static void append(int database, String key, String value) {
		if (StringUtil.isNullOrEmpty(key))
			return;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.append(key, value);
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 在key对应的值后面追加字符串，如果key不存在，则相当于set(key,value)
	 * 
	 * @param key
	 * @param value
	 */
	public static void append(String key, String value) {
		append(DEFAULT_REDIS_DATABASE, key, value);
	}

	/**
	 * 截取key对应的字符串,[start,end]，start和end部分都包括。 <br/>
	 * 若end大于key对应的字符串的最大长度，end为最大长度
	 * 
	 * start = -1 表示从后面截取，即最后一个字符，以此类推
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static String substr(int database, String key, int start, int end) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String result = jedis.substr(key, start, end);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 截取key对应的字符串,[start,end]，start和end部分都包括。 <br/>
	 * 若end大于key对应的字符串的最大长度，end为最大长度
	 * 
	 * start = -1 表示从后面截取，即最后一个字符，以此类推
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static String substr(String key, int start, int end) {
		return substr(DEFAULT_REDIS_DATABASE, key, start, end);
	}

	/**
	 * 设置key的超时时间，若key超时，redis服务器将删除key对应的值
	 * 
	 * @param key
	 * @param seconds
	 */
	public static boolean expire(int database, final String key, final int seconds) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			long result = jedis.expire(key, seconds);
			if (result == 1L)
				return true;
			return false;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 设置key的超时时间，若key超时，redis服务器将删除key对应的值
	 * 
	 * @param key
	 * @param seconds
	 */
	public static boolean expire(final String key, final int seconds) {
		return expire(DEFAULT_REDIS_DATABASE, key, seconds);
	}

	/**
	 * 查询key对应的值在redis中的剩余时间，单位秒
	 * 
	 * @param key
	 * @return -1 没有设置过期时间，或者key不存在
	 */
	public static Long ttl(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return -1L;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.ttl(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 查询key对应的值在redis中的剩余时间，单位秒
	 * 
	 * @param key
	 * @return -1 没有设置过期时间，或者key不存在
	 */
	public static Long ttl(String key) {
		return ttl(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 删除多个key
	 * 
	 * @param keys
	 * @return 0 key不存在，大于1删除成功的key的个数
	 */
	public static Long del(int database, String... keys) {
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.del(keys);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 删除多个key
	 * 
	 * @param keys
	 * @return 0 key不存在，大于1删除成功的key的个数
	 */
	public static Long del(String... keys) {
		return del(DEFAULT_REDIS_DATABASE, keys);
	}

	/**
	 * 重命名一个key
	 * 
	 * @param oldKey
	 * @param newKey
	 * @return
	 */
	public static boolean rename(int database, String oldKey, String newKey) {
		if (StringUtil.isNullOrEmpty(oldKey) || oldKey.equals(newKey))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.rename(oldKey, newKey);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 重命名一个key
	 * 
	 * @param oldKey
	 * @param newKey
	 * @return
	 */
	public static boolean rename(String oldKey, String newKey) {
		return rename(DEFAULT_REDIS_DATABASE, oldKey, newKey);
	}

	/**
	 * 递增一个key对应的整数值，返回递增后的值。 如果key对应的值不存在，<br/>
	 * 或者key对应的值不是数字类型， 则先将key对应的值设为0，然后递增
	 * 
	 * @param key
	 * @return 0 key为空，或者执行异常
	 */
	public static Long incr(int database, String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.incr(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 递增一个key对应的整数值，返回递增后的值。 如果key对应的值不存在，<br/>
	 * 或者key对应的值不是数字类型， 则先将key对应的值设为0，然后递增
	 * 
	 * @param key
	 * @return 0 key为空，或者执行异常
	 */
	public static Long incr(String key) {
		return incr(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * key对应的值加上value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long incrBy(int database, String key, long value) {
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.incrBy(key, value);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * key对应的值加上value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long incrBy(String key, long value) {
		return incrBy(DEFAULT_REDIS_DATABASE, key, value);
	}

	/**
	 * 递减key对应的值，并返回递减后的值。如果key不存在或者key不是数字类型，<br/>
	 * 则先将key对应的值设为0，然后递减
	 * 
	 * @param key
	 * @return
	 */
	public static Long decr(int database, String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.decr(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 递减key对应的值，并返回递减后的值。如果key不存在或者key不是数字类型，<br/>
	 * 则先将key对应的值设为0，然后递减
	 * 
	 * @param key
	 * @return
	 */
	public static Long decr(String key) {
		return decr(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * key对应的值减去value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long decrBy(int database, String key, long value) {
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.decrBy(key, value);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * key对应的值减去value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long decrBy(String key, long value) {
		return decrBy(DEFAULT_REDIS_DATABASE, key, value);
	}

	/**
	 * 
	 * key对应的值为redis中存储的一个hashmap，<br/>
	 * 设置hashmap中的feild 对应的值为value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean hset(int database, String key, String field, String value) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.hset(key, field, value);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 
	 * key对应的值为redis中存储的一个hashmap，<br/>
	 * 设置hashmap中的feild 对应的值为value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean hset(String key, String field, String value) {
		return hset(DEFAULT_REDIS_DATABASE, key, field, value);
	}

	/**
	 * key对应的值为redis中存储的一个hashmap，<br/>
	 * 设置hashmap中的feild 对应的值为value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static <T> boolean hset(int databse, String key, String field, T value) {
		String json = JsonHelper.parseToJson(value);
		return hset(databse, key, field, json);
	}

	/**
	 * key对应的值为redis中存储的一个hashmap，<br/>
	 * 设置hashmap中的feild 对应的值为value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static <T> boolean hset(String key, String field, T value) {
		return hset(DEFAULT_REDIS_DATABASE, key, field, value);
	}

	/**
	 * 获取key对应的hashmap中field对应的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static String hget(int database, String key, String field) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String result = jedis.hget(key, field);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 获取key对应的hashmap中field对应的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static String hget(String key, String field) {
		return hget(DEFAULT_REDIS_DATABASE, key, field);
	}

	/**
	 * 获取key对应的hashmap中field对应的值
	 * 
	 * @param key
	 * @param field
	 * @param clazz
	 * @return
	 */
	public static <T> T hget(int database, String key, String field, Class<T> clazz) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		String json = hget(database, key, field);
		T result = JsonHelper.parseToObject(json, clazz);
		return result;
	}

	/**
	 * 获取key对应的hashmap中field对应的值
	 * 
	 * @param key
	 * @param field
	 * @param clazz
	 * @return
	 */
	public static <T> T hget(String key, String field, Class<T> clazz) {
		return hget(DEFAULT_REDIS_DATABASE, key, field, clazz);
	}

	/**
	 * 保存一个map到redis中key对应的hashmap中
	 * 
	 * @param key
	 * @param hash
	 * @return
	 */
	public static boolean hmset(int database, String key, Map<String, String> hash) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		if (hash == null)
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Map<String, String> map = new HashMap<String, String>(hash.size());
			for (String _key : hash.keySet()) {
				String value = hash.get(_key);
				if (value != null && !"".equals(value))//更新的时候，想置空，就不行了
					map.put(_key, value);
			}
			jedis.hmset(key, map);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 保存一个map到redis中key对应的hashmap中
	 * 
	 * @param key
	 * @param hash
	 * @return
	 */
	public static boolean hmset(String key, Map<String, String> hash) {
		return hmset(DEFAULT_REDIS_DATABASE, key, hash);
	}

	/**
	 * 批量获取key对应的hashmap中fields对应的值
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public static List<String> hmget(int database, String key, String... fields) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		if (fields == null || fields.length == 0)
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			List<String> values = jedis.hmget(key, fields);
			return values;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 批量获取key对应的hashmap中fields对应的值
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public static List<String> hmget(String key, String... fields) {
		return hmget(DEFAULT_REDIS_DATABASE, key, fields);
	}

	/**
	 * 判断key对应的map中是否存在field键
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hexists(int database, String key, String field) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			boolean result = jedis.hexists(key, field);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 判断key对应的map中是否存在field键
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hexists(String key, String field) {
		return hexists(DEFAULT_REDIS_DATABASE, key, field);
	}

	/**
	 * 从key相关的hashmap中删除指定的field键
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public static Long hdel(int database, String key, String... fields) {
		if (StringUtil.isNullOrEmpty(key))
			return 0L;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.hdel(key, fields);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 从key相关的hashmap中删除指定的field键
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	public static Long hdel(String key, String... fields) {
		return hdel(DEFAULT_REDIS_DATABASE, key, fields);
	}

	/**
	 * 查询hashmap的长度
	 * 
	 * @param key
	 * @return
	 */
	public static Long hlen(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return 0L;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.hlen(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 查询hashmap的长度
	 * 
	 * @param key
	 * @return
	 */
	public static Long hlen(String key) {
		return hlen(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 获取key对应的hashmap中所有键('key')
	 * 
	 * @param key
	 * @return
	 */
	public static Set<String> hkeys(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Set<String> result = jedis.hkeys(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 获取key对应的hashmap中所有键('key')
	 * 
	 * @param key
	 * @return
	 */
	public static Set<String> hkeys(String key) {
		return hkeys(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 获取key对应的hashmap中所有值('value')
	 * 
	 * @param key
	 * @return
	 */
	public static List<String> hvalues(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			List<String> result = jedis.hvals(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 获取key对应的hashmap中所有值('value')
	 * 
	 * @param key
	 * @return
	 */
	public static List<String> hvalues(String key) {
		return hvalues(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 获取key对应的hashmap
	 * 
	 * @param key
	 * @return
	 */
	public static Map<String, String> hgetAll(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Map<String, String> result = jedis.hgetAll(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 获取key对应的hashmap
	 * 
	 * @param key
	 * @return
	 */
	public static Map<String, String> hgetAll(String key) {
		return hgetAll(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 增加字符串数据到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← ; rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long rpush(int database, String key, String... strings) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.rpush(key, strings);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 增加字符串数据到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← ; rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long rpush(String key, String... strings) {
		return rpush(DEFAULT_REDIS_DATABASE, key, strings);
	}

	/**
	 * 增加字符串数据到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← ; rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long rpush(int database, String key, Object... objs) {
		if (objs == null)
			return null;
		int length = objs.length;
		String[] strings = new String[length];
		for (int i = 0; i < length; i++) {
			strings[i] = JsonHelper.parseToJson(objs[i]);
		}
		return rpush(database, key, strings);
	}

	/**
	 * 增加字符串数据到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← ; rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long rpush(String key, Object... objs) {
		return rpush(DEFAULT_REDIS_DATABASE, key, objs);
	}

	/**
	 * 增加对象到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← ；rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long lpush(int database, String key, String... strings) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.lpush(key, strings);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 增加对象到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← ；rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long lpush(String key, String... strings) {
		return lpush(DEFAULT_REDIS_DATABASE, key, strings);
	}

	/**
	 * 增加对象到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long lpush(int database, String key, Object... objs) {
		if (objs == null)
			return null;
		int length = objs.length;
		String[] strings = new String[length];
		for (int i = 0; i < length; i++) {
			strings[i] = JsonHelper.parseToJson(objs[i]);
		}
		return lpush(database, key, strings);
	}

	/**
	 * 增加对象到key对应的链表中<br/>
	 * 从尾部添加 lpush 从头部添加，左边 ← rpush 从尾部添加，右边→
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Long lpush(String key, Object... objs) {
		return lpush(DEFAULT_REDIS_DATABASE, key, objs);
	}

	/**
	 * 获取redis链表的长度
	 * 
	 * @param key
	 * @return
	 */
	public static Long llen(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return 0L;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.llen(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 获取redis链表的长度
	 * 
	 * @param key
	 * @return
	 */
	public static Long llen(String key) {
		return llen(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 获取子链表
	 * 
	 * @param key
	 * @param start
	 *            开始位置 从0开始
	 * @param end
	 *            结束位置（包含）
	 * @return
	 */
	public static List<String> lrange(int database, String key, long start, long end) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			List<String> result = jedis.lrange(key, start, end);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 获取子链表
	 * 
	 * @param key
	 * @param start
	 *            开始位置 从0开始
	 * @param end
	 *            结束位置（包含）
	 * @return
	 */
	public static List<String> lrange(String key, long start, long end) {
		return lrange(DEFAULT_REDIS_DATABASE, key, start, end);
	}

	/**
	 * 压缩链表到指定的[start,end]
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean ltrim(int database, String key, long start, long end) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.ltrim(key, start, end);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 压缩链表到指定的[start,end]
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean ltrim(String key, long start, long end) {
		return ltrim(DEFAULT_REDIS_DATABASE, key, start, end);
	}

	/**
	 * 返回链表指定位置的元素
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public static String lindex(int database, String key, long index) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String result = jedis.lindex(key, index);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 返回链表指定位置的元素
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public static String lindex(String key, long index) {
		return lindex(DEFAULT_REDIS_DATABASE, key, index);
	}

	/**
	 * 返回链表指定位置的元素
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public static <T> T lindex(int database, String key, long index, Class<T> toClass) {
		String value = lindex(database, key, index);
		if (value == null)
			return null;
		return JsonHelper.parseToObject(value, toClass);
	}

	/**
	 * 返回链表指定位置的元素
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public static <T> T lindex(String key, long index, Class<T> toClass) {
		return lindex(DEFAULT_REDIS_DATABASE, key, index, toClass);
	}

	/**
	 * 设置链表中index位置的元素
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public static boolean lset(int database, String key, long index, String value) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.lset(key, index, value);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 设置链表中index位置的元素
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public static boolean lset(String key, long index, String value) {
		return lset(DEFAULT_REDIS_DATABASE, key, index, value);
	}

	/**
	 * 设置链表中index位置的元素
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public static <T> boolean lset(int database, String key, long index, T value) {
		if (value == null)
			return false;
		String json = JsonHelper.parseToJson(value);
		return lset(database, key, index, json);
	}

	/**
	 * 设置链表中index位置的元素
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public static <T> boolean lset(String key, long index, T value) {
		return lset(DEFAULT_REDIS_DATABASE, key, index, value);
	}

	/**
	 * 如果count为正，则从左往右（从头部到尾部），删除链表中count个value，<br/>
	 * 若count为0，则全部删除。 如果count为负，则从右往左（从尾部到头部）删除。
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return 成功删除的个数
	 */
	public static Long lrem(int database, String key, long count, String value) {
		if (StringUtil.isNullOrEmpty(key))
			return 0L;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.lrem(key, count, value);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 如果count为正，则从左往右（从头部到尾部），删除链表中count个value，<br/>
	 * 若count为0，则全部删除。 如果count为负，则从右往左（从尾部到头部）删除。
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return 成功删除的个数
	 */
	public static Long lrem(String key, long count, String value) {
		return lrem(DEFAULT_REDIS_DATABASE, key, count, value);
	}

	/**
	 * 删除列表指定索引位置:[start,end]的元素，并返回
	 * 
	 * @param key
	 *            列表对应的key
	 * @param start
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @return
	 */
	public static List<String> lremove(int database, String key, long start, long end) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Transaction tx = jedis.multi();
			tx.lrange(key, start, end);
			tx.ltrim(key, end + 1, -1);
			List<Object> responses = tx.exec();
			List<String> result = (List<String>) responses.get(0);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 删除列表指定索引位置:[start,end]的元素，并返回
	 * 
	 * @param key
	 *            列表对应的key
	 * @param start
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @return
	 */
	public static List<String> lremove(String key, long start, long end) {
		return lremove(DEFAULT_REDIS_DATABASE, key, start, end);
	}

	/**
	 * 返回并删除链表最后一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static String rpop(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String result = jedis.rpop(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 返回并删除链表最后一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static String rpop(String key) {
		return rpop(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 返回并删除链表最后一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T rpop(int database, String key, Class<T> toClass) {
		String json = rpop(database, key);
		if (json == null)
			return null;
		return JsonHelper.parseToObject(json, toClass);
	}

	/**
	 * 返回并删除链表最后一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T rpop(String key, Class<T> toClass) {
		return rpop(DEFAULT_REDIS_DATABASE, key, toClass);
	}

	/**
	 * 返回并删除链表第一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static String lpop(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String result = jedis.lpop(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 返回并删除链表第一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static String lpop(String key) {
		return lpop(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 返回并删除链表第一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T lpop(int database, String key, Class<T> toClass) {
		String json = lpop(database, key);
		if (json == null)
			return null;
		return JsonHelper.parseToObject(json, toClass);
	}

	/**
	 * 返回并删除链表第一个元素
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T lpop(String key, Class<T> toClass) {
		return lpop(DEFAULT_REDIS_DATABASE, key, toClass);
	}

	/**
	 * 添加元素到redis集合（set）中
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long sadd(int database, String key, String... members) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.sadd(key, members);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 添加元素到redis集合（set）中
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long sadd(String key, String... members) {
		return sadd(DEFAULT_REDIS_DATABASE, key, members);
	}

	/**
	 * 添加元素到redis集合（set）中
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long sadd(int database, String key, Object... members) {
		if (members == null)
			return null;
		int length = members.length;
		String[] values = new String[length];
		int index = 0;
		for (int i = 0; i < length; i++) {
			if (members[i] != null)
				values[index++] = JsonHelper.parseToJson(members[i]);
		}
		values = Arrays.copyOf(values, index);
		return sadd(database, key, values);
	}

	/**
	 * 添加元素到redis集合（set）中
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long sadd(String key, Object... members) {
		return sadd(DEFAULT_REDIS_DATABASE, key, members);
	}

	/**
	 * 返回redis集合（set）中所有元素
	 * 
	 * @param key
	 * @return
	 */
	public static Set<String> smembers(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Set<String> result = jedis.smembers(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 返回redis集合（set）中所有元素
	 * 
	 * @param key
	 * @return
	 */
	public static Set<String> smembers(String key) {
		return smembers(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 返回redis集合（set）中所有元素
	 * 
	 * @param key
	 * @return
	 */
	public static <T> Set<T> smembers(int database, String key, Class<T> toClass) {
		Set<String> values = smembers(database, key);
		if (values == null)
			return null;
		Set<T> result = new HashSet<T>();
		for (String value : values) {
			T val = JsonHelper.parseToObject(value, toClass);
			result.add(val);
		}
		return result;
	}

	/**
	 * 返回redis集合（set）中所有元素
	 * 
	 * @param key
	 * @return
	 */
	public static <T> Set<T> smembers(String key, Class<T> toClass) {
		return smembers(DEFAULT_REDIS_DATABASE, key, toClass);
	}

	/**
	 * 从redis集合（set）中删除指定元素
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long srem(int database, String key, String... members) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.srem(key, members);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 从redis集合（set）中删除指定元素
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long srem(String key, String... members) {
		return srem(DEFAULT_REDIS_DATABASE, key, members);
	}

	/**
	 * 从redis集合（set）中删除指定元素
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long srem(int database, String key, Object... members) {
		if (members == null)
			return null;
		int length = members.length;
		String[] values = new String[length];
		int index = 0;
		for (int i = 0; i < length; i++) {
			if (members[i] != null)
				values[index++] = JsonHelper.parseToJson(members[i]);
		}
		values = Arrays.copyOf(values, index);
		return srem(database, key, values);
	}

	/**
	 * 从redis集合（set）中删除指定元素
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public static Long srem(String key, Object... members) {
		return srem(DEFAULT_REDIS_DATABASE, key, members);
	}

	/**
	 * 随机从redis集合中删除一个元素并返回，如果集合不存在或者集合为空，返回null
	 * 
	 * @param key
	 * @return
	 */
	public static String spop(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			String result = jedis.spop(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 随机从redis集合中删除一个元素并返回，如果集合不存在或者集合为空，返回null
	 * 
	 * @param key
	 * @return
	 */
	public static String spop(String key) {
		return spop(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 随机从redis集合中删除一个元素并返回，如果集合不存在或者集合为空，返回null
	 * 
	 * @param key
	 * @param toClass
	 * @return
	 */
	public static <T> T spop(int database, String key, Class<T> toClass) {
		String json = spop(database, key);
		if (json == null)
			return null;
		return JsonHelper.parseToObject(json, toClass);
	}

	/**
	 * 随机从redis集合中删除一个元素并返回，如果集合不存在或者集合为空，返回null
	 * 
	 * @param key
	 * @param toClass
	 * @return
	 */
	public static <T> T spop(String key, Class<T> toClass) {
		return spop(DEFAULT_REDIS_DATABASE, key, toClass);
	}

	/**
	 * 从原集合移动指定元素到目标集合中，原子操作，线程安全
	 * 
	 * @param srcKey
	 *            原集合对应的key
	 * @param destKey
	 *            目标集合对应的key
	 * @param member
	 *            元素
	 * @return
	 */
	public static boolean smove(int database, String srcKey, String destKey, String member) {
		if (StringUtil.isNullOrEmpty(srcKey) || StringUtil.isNullOrEmpty(destKey))
			return false;
		if (member == null)
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.smove(srcKey, destKey, member);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 从原集合移动指定元素到目标集合中，原子操作，线程安全
	 * 
	 * @param srcKey
	 *            原集合对应的key
	 * @param destKey
	 *            目标集合对应的key
	 * @param member
	 *            元素
	 * @return
	 */
	public static boolean smove(String srcKey, String destKey, String member) {
		return smove(DEFAULT_REDIS_DATABASE, srcKey, destKey, member);
	}

	/**
	 * 从原集合移动指定元素到目标集合中，原子操作，线程安全
	 * 
	 * @param srcKey
	 *            原集合对应的key
	 * @param destKey
	 *            目标集合对应的key
	 * @param member
	 *            元素
	 * @return
	 */
	public static <T> boolean smove(int database, String srcKey, String destKey, T member) {
		String value = JsonHelper.parseToJson(member);
		return smove(database, srcKey, destKey, value);
	}

	/**
	 * 从原集合移动指定元素到目标集合中，原子操作，线程安全
	 * 
	 * @param srcKey
	 *            原集合对应的key
	 * @param destKey
	 *            目标集合对应的key
	 * @param member
	 *            元素
	 * @return
	 */
	public static <T> boolean smove(String srcKey, String destKey, T member) {
		return smove(DEFAULT_REDIS_DATABASE, srcKey, destKey, member);
	}

	/**
	 * 获取redis集合中的元素个数
	 * 
	 * @param key
	 * @return
	 */
	public static Long slen(int database, String key) {
		if (StringUtil.isNullOrEmpty(key))
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Long result = jedis.scard(key);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 获取redis集合中的元素个数
	 * 
	 * @param key
	 * @return
	 */
	public static Long slen(String key) {
		return slen(DEFAULT_REDIS_DATABASE, key);
	}

	/**
	 * 集合匹配元素， 判断member是否为集合中的元素
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public static boolean sismember(int database, String key, String member) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Boolean result = jedis.sismember(key, member);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 集合匹配元素， 判断member是否为集合中的元素
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public static boolean sismember(String key, String member) {
		return sismember(DEFAULT_REDIS_DATABASE, key, member);
	}

	/**
	 * 集合匹配元素， 判断member是否为集合中的元素
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public static <T> boolean sismember(int database, String key, T member) {
		String json = JsonHelper.parseToJson(member);
		return sismember(database, key, json);
	}

	/**
	 * 集合匹配元素， 判断member是否为集合中的元素
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public static <T> boolean sismember(String key, T member) {
		return sismember(DEFAULT_REDIS_DATABASE, key, member);
	}

	/**
	 * 求集合的交集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static Set<String> sinter(int database, String... keys) {
		if (keys == null)
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Set<String> result = jedis.sinter(keys);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 求集合的交集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static Set<String> sinter(String... keys) {
		return sinter(DEFAULT_REDIS_DATABASE, keys);
	}

	/**
	 * 求集合的交集，结果存储到新的集合（destKey对应的集合）中去
	 * 
	 * @param dstkey
	 * @param keys
	 * @return
	 */
	public static boolean sinterstore(int database, final String dstKey, final String... keys) {
		if (StringUtil.isNullOrEmpty(dstKey) || keys == null)
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.sinterstore(dstKey, keys);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 求集合的交集，结果存储到新的集合（destKey对应的集合）中去
	 * 
	 * @param dstkey
	 * @param keys
	 * @return
	 */
	public static boolean sinterstore(final String dstKey, final String... keys) {
		return sinterstore(DEFAULT_REDIS_DATABASE, dstKey, keys);
	}

	/**
	 * 求集合的交集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static <T> Set<T> sinter(int database, Class<T> toClass, String... keys) {
		Set<String> set = sinter(database, keys);
		if (set == null)
			return null;
		Set<T> result = new HashSet<T>();
		for (String json : set) {
			T value = JsonHelper.parseToObject(json, toClass);
			result.add(value);
		}
		return result;
	}

	/**
	 * 求集合的交集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static <T> Set<T> sinter(Class<T> toClass, String... keys) {
		return sinter(DEFAULT_REDIS_DATABASE, toClass, keys);
	}

	/**
	 * 求集合的并集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static Set<String> sunion(int database, String... keys) {
		if (keys == null)
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Set<String> result = jedis.sunion(keys);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 求集合的并集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static Set<String> sunion(String... keys) {
		return sunion(DEFAULT_REDIS_DATABASE, keys);
	}

	/**
	 * 求集合的并集，结果存储到新的集合（destKey对应的集合）中去
	 * 
	 * @param dstkey
	 * @param keys
	 * @return
	 */
	public static boolean sunionstore(int database, final String dstKey, final String... keys) {
		if (StringUtil.isNullOrEmpty(dstKey) || keys == null)
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.sunionstore(dstKey, keys);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 求集合的并集，结果存储到新的集合（destKey对应的集合）中去
	 * 
	 * @param dstkey
	 * @param keys
	 * @return
	 */
	public static boolean sunionstore(final String dstKey, final String... keys) {
		return sunionstore(DEFAULT_REDIS_DATABASE, dstKey, keys);
	}

	/**
	 * 求集合的交集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static <T> Set<T> sunion(int database, Class<T> toClass, String... keys) {
		Set<String> set = sunion(database, keys);
		if (set == null)
			return null;
		Set<T> result = new HashSet<T>();
		for (String json : set) {
			T value = JsonHelper.parseToObject(json, toClass);
			result.add(value);
		}
		return result;
	}

	/**
	 * 求集合的交集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static <T> Set<T> sunion(Class<T> toClass, String... keys) {
		return sunion(DEFAULT_REDIS_DATABASE, toClass, keys);
	}

	/**
	 * 求集合的差集
	 * 
	 * <pre>
	 * key1 = [x, a, b, c]
	 * key2 = [c]
	 * key3 = [a, d]
	 * SDIFF key1,key2,key3 => [x, b]
	 * </pre>
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static Set<String> sdiff(int database, String... keys) {
		if (keys == null)
			return null;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Set<String> result = jedis.sdiff(keys);
			return result;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 求集合的差集
	 * 
	 * <pre>
	 * key1 = [x, a, b, c]
	 * key2 = [c]
	 * key3 = [a, d]
	 * SDIFF key1,key2,key3 => [x, b]
	 * </pre>
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static Set<String> sdiff(String... keys) {
		return sdiff(DEFAULT_REDIS_DATABASE, keys);
	}

	/**
	 * 求集合的交集，结果存储到新的集合（destKey对应的集合）中去
	 * 
	 * @param dstkey
	 * @param keys
	 * @return
	 */
	public static boolean sdiffstore(int database, final String dstKey, final String... keys) {
		if (StringUtil.isNullOrEmpty(dstKey) || keys == null)
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			jedis.sdiffstore(dstKey, keys);
			return true;
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 求集合的交集，结果存储到新的集合（destKey对应的集合）中去
	 * 
	 * @param dstkey
	 * @param keys
	 * @return
	 */
	public static boolean sdiffstore(final String dstKey, final String... keys) {
		return sdiffstore(DEFAULT_REDIS_DATABASE, dstKey, keys);
	}

	/**
	 * 求集合的差集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static <T> Set<T> sdiff(int database, Class<T> toClass, String... keys) {
		Set<String> set = sdiff(database, keys);
		if (set == null)
			return null;
		Set<T> result = new HashSet<T>();
		for (String json : set) {
			T value = JsonHelper.parseToObject(json, toClass);
			result.add(value);
		}
		return result;
	}

	/**
	 * 求集合的差集
	 * 
	 * @param keys
	 *            一个key对应一个集合
	 * @return
	 */
	public static <T> Set<T> sdiff(Class<T> toClass, String... keys) {
		return sdiff(DEFAULT_REDIS_DATABASE, toClass, keys);
	}

	/**
	 * 批量插入数据到redis集合（set)中
	 * 
	 * @param key
	 *            集合对应的Key
	 * @param values
	 *            数据列表
	 */
	public static void multiSAdd(int database, String key, List<String> values) {
		if (values == null || values.isEmpty())
			return;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Transaction tx = jedis.multi();
			for (String value : values) {
				tx.sadd(key, value);
			}
			tx.exec();
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 批量插入数据到redis集合（set)中
	 * 
	 * @param key
	 *            集合对应的Key
	 * @param values
	 *            数据列表
	 */
	public static void multiSAdd(String key, List<String> values) {
		multiSAdd(DEFAULT_REDIS_DATABASE, key, values);
	}

	/**
	 * 更新redis集合，先删除key，然后设置key对应的集合的值为values
	 * 
	 * @param key
	 * @param values
	 */
	public static void updateSet(int database, String key, Set<String> values) {
		if (values == null || values.isEmpty())
			return;
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Transaction tx = jedis.multi();
			tx.del(key);
			String[] member = values.toArray(new String[values.size()]);
			tx.sadd(key, member);
			tx.exec();
		} finally {
			if (jedis != null)
				closeJedis(database, jedis);
		}
	}

	/**
	 * 更新redis集合，先删除key，然后设置key对应的集合的值为values
	 * 
	 * @param key
	 * @param values
	 */
	public static void updateSet(String key, Set<String> values) {
		updateSet(DEFAULT_REDIS_DATABASE, key, values);
	}

	/**
	 * 批量判断Redis key所指向的集合中是否存在values列表中的数据;采用单线程方式<br/>
	 * 如果value存在，返回true，否则false
	 * 
	 * @param key
	 *            Redis集合的key
	 * @param values
	 *            需要匹配的值列表
	 * @return
	 */
	public static List<Object> multiSismember(int database, String key, List<String> values) {
		if (values == null || values.isEmpty())
			return null;

		List<Object> returnList = new ArrayList<Object>();
		if (values.size() <= DEFAULT_MATCH_SIZE) {
			List<Object> result = innerMatch(database, key, values);
			if (result != null)
				returnList.addAll(result);
			return returnList;
		}

		List<List<String>> lists = partition(values, DEFAULT_MATCH_SIZE);
		for (List<String> list : lists) {
			List<Object> result = innerMatch(database, key, list);
			if (result != null)
				returnList.addAll(result);
		}

		return returnList;
	}

	/**
	 * 批量判断Redis key所指向的集合中是否存在values列表中的数据;采用单线程方式<br/>
	 * 如果value存在，返回true，否则false
	 * 
	 * @param key
	 *            Redis集合的key
	 * @param values
	 *            需要匹配的值列表
	 * @return
	 */
	public static List<Object> multiSismember(String key, List<String> values) {
		return multiSismember(DEFAULT_REDIS_DATABASE, key, values);
	}

	/**
	 * 匹配列表
	 * @param database
	 * @param key
	 * @param values
	 * @return
	 */
	private static List<Object> innerMatch(int database, String key, List<String> values) {
		List<Object> returnList = new ArrayList<Object>();
		Jedis jedis = null;
		try {
			jedis = getJedis(database);
			Transaction tx = jedis.multi();
			for (String value : values) {
				tx.sismember(key, value);
			}
			List<Object> result = tx.exec();
			if (result != null && !result.isEmpty())
				returnList.addAll(result);
			return returnList;
		} finally {
			closeJedis(database, jedis);
		}
	}

	/**
	 * 拆分列表
	 * 
	 * @param values
	 *            源列表
	 * @param devideSize
	 *            拆分大小
	 * @return
	 */
	private static List<List<String>> partition(List<String> values, int devideSize) {
		int size = values.size();
		int resultSize = (int) Math.ceil(1.0 * size / devideSize);
		List<List<String>> result = new ArrayList<List<String>>(resultSize);
		for (int i = 0; i < resultSize; i++) {
			int start = i * devideSize;
			int end = Math.min(start + devideSize, size);
			List<String> subList = values.subList(start, end);
			result.add(subList);
		}
		return result;
	}
	
	/** set Object */
	public static boolean setObject(String key, Object object, int time) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(0);
			jedis.setex(key.getBytes(), time, serialize(object));
			return true;
		} finally {
			if (jedis != null)
				closeJedis(0, jedis);
		}
	}

	/** get Object */
	public static Object getObject(String key) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(0);
			byte[] value = jedis.get(key.getBytes());
			return unserialize(value);
		} finally {
			if (jedis != null)
				closeJedis(0, jedis);
		}
	}

	/** delete a key **/
	public static boolean delObject(String key) {
		if (StringUtil.isNullOrEmpty(key))
			return false;
		Jedis jedis = null;
		try {
			jedis = getJedis(0);
			return jedis.del(key.getBytes()) > 0;
		} finally {
			if (jedis != null)
				closeJedis(0, jedis);
		}
	}
	
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {

		}
		return null;
	}

	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {

		}
		return null;
	}
	
	public static void main(String[] args) {
		Map m = new HashMap();
		m.put("rain","123");
		/*JsonSerializableObject object = new JsonSerializableObject();
		object.setObject(m);*/
		RedisFacade.set("test","");
		System.out.println(RedisFacade.get("test",Map.class));
		
		System.out.println(RedisFacade.get("TestManager_123",Map.class));
	}
	
}
