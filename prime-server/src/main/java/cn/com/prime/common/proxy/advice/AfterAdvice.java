package cn.com.prime.common.proxy.advice;

import java.lang.reflect.Method;
/**
 * 事后执行
 * @author Rain
 *
 */
public interface AfterAdvice {
	
	/**
	 * 事后执行
	 * @param o
	 * @param method
	 * @param args
	 * @param result
	 */
	void after(Object o,Method method,Object [] args,Object result);
	
	/**
	 * 是否需要在finnaly中执行
	 * @return
	 */
	boolean isFinally();
	
}
