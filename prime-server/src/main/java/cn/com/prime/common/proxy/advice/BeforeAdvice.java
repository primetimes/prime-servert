package cn.com.prime.common.proxy.advice;

import java.lang.reflect.Method;

public interface BeforeAdvice {
	
    void before(Object o,Method method,Object [] args);
	
}
