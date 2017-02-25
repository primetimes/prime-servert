/**
 * 2014年3月21日下午6:16:24
 * 
 * Copyright 2009-2014 Flaginfo , Inc. All rights reserved.
 * FLAGINFO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cn.com.prime.common.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内置线程安全的缓存对象Map容器
 * 
 * @author ming.tan@flaginfo.com.cn
 * @date 2014年3月21日
 * @time 下午6:16:24
 */
public class ObjectCache<K, V> {

	private final Logger log = LoggerFactory.getLogger(ObjectCache.class);

	private final Map<K, CacheElement<V>> innerMap = new ConcurrentHashMap<K, CacheElement<V>>();

	public static <K, V> ObjectCache<K, V> newObjectCache(boolean autoEvict) {
		return new ObjectCache<K, V>(autoEvict);
	}
	
	public static <K, V> ObjectCache<K, V> newObjectCache() {
		return new ObjectCache<K, V>();
	}

	/**
	 * 创建一个被动删除过期的对象缓存容器
	 */
	private ObjectCache() {
		this(false);
	}

	/**
	 * 根据主动删除过期对象标志：autoEvict，创建对象缓存容器
	 * 
	 * @param autoEvict
	 *            自动删除过期缓存对象
	 */
	private ObjectCache(boolean autoEvict) {
		if (autoEvict)
			init();
	}

	/**
	 * 初始化对象缓存容器<br/>
	 * <ul>
	 * <li>创建一个线程池，定期清理过期缓存对象</li>
	 * <li>创建一个JVM关闭钩子，应用退出时清理缓存和线程池</li>
	 * </ul>
	 */
	public void init() {
		final ScheduledExecutorService executor = Executors
				.newScheduledThreadPool(1);
		final Runnable command = new Runnable() {

			@Override
			public void run() {
				try {
					log.debug("开始定时清理过期缓存对象......");
					for (K key : innerMap.keySet()) {
						CacheElement<V> elem = innerMap.get(key);
						if (elem.isExpired()) {
							log.info("缓存对象：" + elem + "被清理");
							innerMap.remove(key);
						}
					}
					log.debug("清理过期缓存对象完成......");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		executor.scheduleAtFixedRate(command, 5, 1, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				log.info("应用系统退出，开始清理所有缓存对象......");
				ObjectCache.this.destroy();
				executor.shutdownNow();
				log.info("清理缓存对象完成，Bye......");
			}

		});
	}

	/**
	 * 清理所有缓存对象
	 */
	public void destroy() {
		innerMap.clear();
	}

	/**
	 * 从缓存中获取数据
	 * 
	 * @param key
	 *            键
	 * @return 缓存数据
	 */
	public V get(K key) {
		final CacheElement<V> element = innerGet(key);
		if (element == null)
			return null;
		if (key != null && element.isExpired()) {
			remove(key);
			return null;
		}
		return element.getValue();
	}

	/**
	 * 缓存对象并设置对象的缓存时间
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheTime
	 *            对象缓存时间或存活时间
	 */
	public void set(K key, V value, long cacheTime) {
		set(key, value, cacheTime, TimeUnit.MILLISECONDS);
	}

	/**
	 * 缓存对象并设置缓存时间，以及时间单位
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheTime
	 *            缓存时间
	 * @param timeUnit
	 *            缓存时间单位
	 */
	public void set(K key, V value, long cacheTime, TimeUnit timeUnit) {
		if (key == null)
			return;
		if (cacheTime < 0)
			cacheTime = -1L;
		long ttl = timeUnit.toMillis(cacheTime);
		CacheElement<V> elem = new CacheElement<V>(value, ttl);
		innerMap.put(key, elem);
	}

	/**
	 * 缓存对象
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void set(K key, V value) {
		put(key, value);
	}

	private CacheElement<V> innerGet(K key) {
		return innerMap.get(key);
	}

	/**
	 * 根据对象缓存时间
	 * 
	 * @param key
	 *            键
	 * @return 缓存时间
	 */
	public long getCacheTime(K key) {
		final CacheElement<V> element = innerGet(key);
		if (element == null)
			return 0L;
		return System.currentTimeMillis() - element.getCreateTime();
	}

	public boolean containsKey(K key) {
		return innerMap.containsKey(key);
	}

	public int size() {
		return innerMap.size();
	}

	public boolean isEmpty() {
		return innerMap.isEmpty();
	}

	public void clear() {
		innerMap.clear();
	}

	public void put(K key, V value) {
		if (key == null)
			return;
		final CacheElement<V> item = new CacheElement<V>(value);
		innerMap.put(key, item);
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		if (map == null || map.size() == 0)
			return;
		for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = map
				.entrySet().iterator(); i.hasNext();) {
			Map.Entry<? extends K, ? extends V> e = i.next();
			put(e.getKey(), e.getValue());
		}
	}

	public V remove(K key) {
		CacheElement<V> item = innerMap.remove(key);
		return item.getValue();
	}

	public Set<K> keySet() {
		return innerMap.keySet();
	}

	public Collection<V> values() {
		if (size() == 0)
			return null;
		final Collection<CacheElement<V>> collection = innerMap.values();
		Collection<V> result = new ArrayList<V>();
		for (CacheElement<V> e : collection) {
			result.add(e.getValue());
		}
		return result;
	}

	public static final class CacheElement<V> {
		private final V value;
		private final long creatTime;
		// time to live
		private long ttl;

		public CacheElement(final V value) {
			this(value, -1);
		}

		public CacheElement(final V value, long ttl) {
			this.value = value;
			this.creatTime = System.currentTimeMillis();
			this.ttl = ttl;
		}

		public V getValue() {
			return this.value;
		}

		public Long getCreateTime() {
			return creatTime;
		}

		public long getTtl() {
			return ttl;
		}

		public void setTtl(long ttl) {
			this.ttl = ttl;
		}

		public boolean isExpired() {
			if (this.ttl == -1L)
				return false;
			return creatTime + ttl < System.currentTimeMillis();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (creatTime ^ (creatTime >>> 32));
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheElement other = (CacheElement) obj;
			if (creatTime != other.creatTime)
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "{value:" + value.toString() + ",createTime:" + creatTime
					+ ",ttl:" + ttl + "}";
		}

	}
}
