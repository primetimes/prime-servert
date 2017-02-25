package cn.com.prime.common.db;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * DB事务控制
 * @author Rain
 *
 */
public class DBTransactionInterceptor implements MethodInterceptor {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Object intercept(Object object, Method method, Object[] o,
			MethodProxy proxy) throws Throwable {
		
		Object result = null;
		long start = System.currentTimeMillis();
		//String methodKey =method.getDeclaringClass().getName()+"@"+method.getName();
		String methodKey =method.getDeclaringClass().getName()+"@"+method.getName()+"@"+method.getGenericParameterTypes().length+"@"+method.getReturnType().getName();
		//logger.info("methodKey="+methodKey);
		try {
			TransactionHolder.push(methodKey);
			result = proxy.invokeSuper(object, o);
			TransactionHolder.pull();
			if(TransactionHolder.isEmpty()){
				ConnectionHolder.commit();
				logger.info("cost time:"+methodKey+"==>"+(System.currentTimeMillis()-start)+"ms");
			}else{
				if(System.currentTimeMillis()-start>100){ //查询过慢的连接先关闭掉
					//关闭自动提交的连接，例如查询的完成后可以关闭连接了，方便给其他线程使用，提高性能
					//ConnectionHolder.colseAutoCommit();
				}
			}
			if(System.currentTimeMillis()-start>500){
				logger.info("****"+methodKey+" cost time:"+(System.currentTimeMillis()-start)+"ms. The method is more slowly ****");
			}
		} catch (Exception e) {
			//e.printStackTrace();
			TransactionHolder.remove();
			ConnectionHolder.rollback();
			throw new RuntimeException(e);
		}finally{
			//防止异常状况，没有关闭连接
			if(TransactionHolder.isEmpty()){
				ConnectionHolder.colseAndRemove();
			}
		}
		return result;
	}

}
