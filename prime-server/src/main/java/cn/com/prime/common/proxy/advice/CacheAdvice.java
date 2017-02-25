package cn.com.prime.common.proxy.advice;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.prime.common.proxy.InvokeResult;
import cn.com.prime.common.proxy.annotation.Cache;
import cn.com.prime.common.proxy.annotation.Cache.OperateType;
import cn.com.prime.common.util.CacheUtil;

/**
 * CacheAdvice
 * 策略更改，将Cache时间延迟，默认缓存2天的过期时间
 * 通过比较时间来判定是否数据失效，这样在DB发生问题的时候发挥作用。
 * 方法对递归缓存支持不好
 * @author Rain
 *
 */
public class CacheAdvice implements BeforeAdvice,AfterAdvice {

	private static Logger logger = LoggerFactory.getLogger(CacheAdvice.class);
	//设置最大失效时间
	private static long MAX_EXPIRE_SEC = 2*24*60*60;
	
	@Override
	public void before(Object o, Method method, Object[] args) {
		Cache dc = method.getAnnotation(Cache.class);
		if(dc==null){
			return;
		}
		InvokeResult.remove();
		String cacheKey = getCacheKey(dc,args);
		OperateType operateType = dc.operateType();
		boolean isExist = CacheUtil.exists(cacheKey);
		//GET 操作
		if(OperateType.GET == operateType && isExist){
			Object result = CacheUtil.get(cacheKey,method.getReturnType());
			InvokeResult cacheResult = InvokeResult.get();
			if(cacheResult == null){
				cacheResult = new InvokeResult();
			}
			cacheResult.setObject(method,result);
			InvokeResult.set(cacheResult);
			
			long leftTime = CacheUtil.ttl(cacheKey);
			if(leftTime != -1 && (MAX_EXPIRE_SEC - leftTime)>(dc.ttl()/1000)){ //未过期
				cacheResult.setExpired(method,true);
				logger.info("expired the key"+cacheKey);
			}else{
				logger.info("hit cache key:"+cacheKey);
			}
		}
	
	}
	
	@Override
	public void after(Object o, Method method, Object[] args, Object result) {
		
		Cache dc = method.getAnnotation(Cache.class);
		if(dc==null){
			return;
		}
		String cacheKey = getCacheKey(dc,args);
		OperateType operateType = dc.operateType();
		if(OperateType.DELETE == operateType){
			CacheUtil.del(cacheKey);
			return;
		}
		
		if(dc.ignoreNull() && result == null){
			return;
		}
		//非缓存数据才更新数据
		if(InvokeResult.isNull(method) || InvokeResult.get().isExpired(method)){
			CacheUtil.set(cacheKey, (result == null? "" : result), MAX_EXPIRE_SEC*1000);
		}
	}
	
	@Override
	public boolean isFinally() {
		return false;
	}
	
	private String getCacheKey(Cache dc,Object []args){
		String cacheKey = dc.cacheKey();
		cacheKey = MessageFormat.format(cacheKey,args);
		return cacheKey;
	}

}
