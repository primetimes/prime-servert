package cn.com.prime.common.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用结果对象
 * 同时提供静态方法存储
 * @author Rain
 *
 */
public class InvokeResult {
	
	private Map<String,Object> objectMap = new HashMap<String,Object>();
	
	private Map<String,Boolean> objectExpireMap = new HashMap<String,Boolean>();
	
	
	public boolean isExpired(Method m) {
		return objectExpireMap.get(this.getMethodKey(m));
	}

	private static final ThreadLocal<InvokeResult> resultLocal = new ThreadLocal<InvokeResult>();
	
	public static InvokeResult get(){
		return resultLocal.get();
	}
	
	public static boolean isNull(){
		return resultLocal.get() == null ? true:false;
	}
	
	public static boolean isNull(Method m){
		if(isNull()){
			return true;
		}
		return resultLocal.get().getObject(m) == null ? true:false;
	}
	
	public static void remove(){
		resultLocal.remove();
	}
	
	
	public static void set(InvokeResult result){
		resultLocal.set(result);
	}
	
	private String getMethodKey(Method m){
		return m.getClass().getName()+"@"+m.getName();
	}
	
	public void setObject(Method m,Object object) {
		this.objectMap.put(getMethodKey(m),object);
		this.objectExpireMap.put(this.getMethodKey(m),false);
	}
	
	public Object getObject(Method m) {
		return this.objectMap.get(getMethodKey(m));
	}
	
	public void setExpired(Method m,boolean expired) {
		this.objectExpireMap.put(this.getMethodKey(m),expired);
	}
	
	
	
	
	
}
