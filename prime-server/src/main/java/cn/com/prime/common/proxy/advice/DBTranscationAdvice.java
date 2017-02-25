package cn.com.prime.common.proxy.advice;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.prime.common.db.ConnectionHolder;
import cn.com.prime.common.proxy.MethodStackHolder;

/**
 * 关系数据库事务拦截，
 * 按照当前mangaer方法栈的状态来区分
 * @author Rain
 *
 */
public class DBTranscationAdvice implements BeforeAdvice,AfterAdvice,ThrowAdvice{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void before(Object o, Method method, Object[] args) {
		//String methodKey =method.getDeclaringClass().getName()+"@"+method.getName();
		//TransactionHolder.push(methodKey);
	}
	
	@Override
	public void after(Object o, Method method, Object[] args, Object result) {
		//String methodKey = (String)TransactionHolder.pull();
		try {
			if(MethodStackHolder.isEmpty()){
				ConnectionHolder.commit();
				logger.debug("methodKey="+method.getClass().getName()+"@"+method.getName() +" commit.");
			}
		} catch (Exception e) {
			exception(o,method,args,e);
			throw new RuntimeException(e);
		}finally{
			if(MethodStackHolder.isEmpty()){
				ConnectionHolder.colseAndRemove();
			}
		}
	}
	
	/**
	 * 异常调用
	 * exception call
	 */
	@Override
	public void exception(Object o, Method method, Object[] args, Exception e) {
		logger.info("异常，回滚本次提交");
		MethodStackHolder.remove();
		ConnectionHolder.rollback();
	}
	
	@Override
	public boolean isFinally() {
		return true;
	}
	
}
