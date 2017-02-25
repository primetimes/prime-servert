package cn.com.prime.common.support;

import cn.com.prime.common.util.RedisFacade;

/**
 * Redis实现分布式锁
 * @author Rain
 *
 */
public class RedisLock implements Lock{
	
	
	/**
	 * 加锁的Key
	 * key
	 */
	private String key;
	
	/**
	 * 最大等待时间，如果超过设置的时间就会自动删除key来获取锁
	 */
	private long ttl;
	
	private RedisLock(String key,long ttl){
		this.key = key;
		this.ttl = ttl;
		getLock(); 
	}
	
	
	/**
	 * 获取redis锁
	 * @param key
	 * @param ttl
	 * @return
	 */
	public static RedisLock getLock(String key,long ttl){
		return new RedisLock(key,ttl);
	}
	
	/**
	 * 获取锁
	 */
	public boolean getLock(){
		boolean b = false;
		long start = System.currentTimeMillis();
		int c = 0;
		while(!b){
			//使用key作为Redis的value存储
			b = RedisFacade.setnx(key, key);
			RedisFacade.expire(key,(int)(this.ttl/1000));
			if(b){
				break;
			}
			if(System.currentTimeMillis()-start>=this.ttl){
				releaseLock();
			}else{
				try {
					System.out.println("循环获取锁,c="+(c++));
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}
	
	public boolean releaseLock(){
		return RedisFacade.delObject(key);
	}

}
