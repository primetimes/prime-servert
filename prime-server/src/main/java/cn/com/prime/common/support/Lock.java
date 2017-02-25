package cn.com.prime.common.support;

public interface Lock {
	
	/**
	 * 获取锁
	 * @return
	 */
	public boolean getLock();
	
	/**
	 * 释放锁
	 * @return
	 */
	public boolean releaseLock();

}
