package cn.com.prime.common.proxy.advice;

import java.lang.reflect.Method;

/**
 * 异常通知
 * 
 * @author Rain
 *
 */
public interface ThrowAdvice {
	
	void exception(Object o,Method method,Object [] args,Exception e);
	
}
