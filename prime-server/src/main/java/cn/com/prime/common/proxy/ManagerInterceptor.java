package cn.com.prime.common.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.prime.common.proxy.advice.AfterAdvice;
import cn.com.prime.common.proxy.advice.BeforeAdvice;
import cn.com.prime.common.proxy.advice.CacheAdvice;
import cn.com.prime.common.proxy.advice.DBTranscationAdvice;
import cn.com.prime.common.proxy.advice.ThrowAdvice;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Manager拦截器<br/>
 * 可执行多个配置的Advice<br/>
 * 添加Advice到List中即可自动执行
 * @author Rain
 *
 */
public class ManagerInterceptor implements MethodInterceptor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static List<Object> ARROUND_ADVICE = new ArrayList<Object>();
	
	static{
		ARROUND_ADVICE.add(new CacheAdvice());
		ARROUND_ADVICE.add(new DBTranscationAdvice());
	}
	
	@Override
	public Object intercept(Object object, Method method, Object[] o,
			MethodProxy proxy) throws Throwable {
		
		Object result = null;
		long start = System.currentTimeMillis();
		
		try {
			String methodKey = method.getDeclaringClass().getName()+"@"+method.getName();
			MethodStackHolder.push(methodKey);
			
			doBefore(object,method,o,proxy);
			//如果InvokeResult已经放置了结果，则直接返回该结果
			if(InvokeResult.isNull(method) || InvokeResult.get().isExpired(method)){
				result = proxy.invokeSuper(object, o);
			}else{
				result = InvokeResult.get().getObject(method);
			}
			doAfter(object, method, o,result);
		} catch (Exception e) {
			e.printStackTrace();
			doThrows(object,method,o,e);
			//若方法异常了，但是有过期缓存，则直接使用
			if(!InvokeResult.isNull(method)){
				logger.info("方法Exception,使用Cache返回");
				e.printStackTrace();
				result = InvokeResult.get().getObject(method);
			}else{
				throw new RuntimeException(e);
			}
		}finally{
			
			MethodStackHolder.pull();
			
			//执行finally的
			doFinallyAfter(object, method, o, result);
			
			if(MethodStackHolder.isEmpty()){
				InvokeResult.remove();
				MethodStackHolder.remove();
			}
			
		}
		
		if(System.currentTimeMillis()-start>1000){
			String methodKey = method.getDeclaringClass().getName()+"@"+method.getName();
			logger.info("method slowly="+methodKey+" cost time:"+(System.currentTimeMillis()-start)+" ms");
		}
		
		return result;
	}
	
	private void doBefore(Object object, Method method, Object[] o,
			MethodProxy proxy){
		for(Object ba:ARROUND_ADVICE){
			if(ba instanceof BeforeAdvice){
				((BeforeAdvice)ba).before(proxy, method, o);
			}	
		}
	}
	
	private void doAfter(Object object, Method method, Object[] o,Object result){
		int size = ARROUND_ADVICE.size();
		for(int i=size-1;i>=0;i--){
			Object aa = ARROUND_ADVICE.get(i);
			if(aa instanceof AfterAdvice){
				if(!((AfterAdvice)aa).isFinally()){
					((AfterAdvice)aa).after(object, method, o, result);
				}
			}
		}
	}
	
	private void doFinallyAfter(Object object, Method method, Object[] o,Object result){
		
		int size = ARROUND_ADVICE.size();
		for(int i=size-1;i>=0;i--){
			Object aa = ARROUND_ADVICE.get(i);
			if(aa instanceof AfterAdvice){
				if(((AfterAdvice)aa).isFinally()){
					try {
						((AfterAdvice)aa).after(object, method, o, result);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	private void doThrows(Object object, Method method, Object[] args,Exception e){
		int size = ARROUND_ADVICE.size();
		for(int i=size-1;i>=0;i--){
			Object aa = ARROUND_ADVICE.get(i);
			if(aa instanceof ThrowAdvice){
				try {
					((ThrowAdvice)aa).exception(object, method, args, e);
				} catch (Exception e1) {
					logger.info(e.getMessage(),e);
					e1.printStackTrace();
				}
			}
		}
	}
	
}
