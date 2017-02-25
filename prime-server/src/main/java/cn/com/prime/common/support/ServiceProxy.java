package cn.com.prime.common.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.com.prime.common.proxy.ManagerInterceptor;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;

/**
 * 类工厂，所有的Manager都通过这个方法获得
 * 5.3 DBTransactionInterceptor替换成ManagerInterceptor
 * 可执行更多的拦截，具体参看proxy包
 * @author Rain
 *
 */
public class ServiceProxy {
	
	private static Map<String,Object> beanMap = new HashMap<String,Object>();
	
	public static <T> T getInstance(Class beanClazz){
		 T t = (T)beanMap.get(beanClazz.getName());
		 if(t==null){
			 Enhancer en = new Enhancer();
			 en.setSuperclass(beanClazz);
			 en.setCallback(new ManagerInterceptor()); //DBTransactionInterceptor
			 en.setCallbackFilter(new CallbackFilter() {
				@Override
				public int accept(Method arg0) {
					return 0;
				}
			});
			 t = (T)en.create();
			 beanMap.put(beanClazz.getName(),t);
		 }
		 return t;
	}
	
	
	/**
	 * className去实例化对象
	 * @param clazzName
	 * @return
	 */
	public static <T> T getInstance(String clazzName){
		try {
			return getInstance(Class.forName(clazzName));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
