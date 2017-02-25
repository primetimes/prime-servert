package cn.com.prime.common.proxy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
	
	/**
	 * 一刻钟
	 */
	public static final long QUANTER = 15*60*1000;
	
	/**
	 * 一个小时
	 */
	public static final long HOUR = 60*60*1000;
	
	/**
	 * 1天的缓存
	 */
	public static final long DAY = 24*60*60*1000;
	
	
	/**
	 * 缓存Key<br/>
	 * 可动态从方法的参数中获取<br/>
	 * ,形如：key_${1}_${2},那么${1}和${2}为参数的方法中参数的第n个，下标从0开始
	 * @return
	 */
	public String cacheKey();
	
	/**
	 * cache的操作类型<br/>
	 * GET默认值，如果存在数据从缓存获取，不存在执行方法后再缓存<br/>
	 * UPDATE表示执行方法后，将结果缓存到内存中<br/>
	 * DELETE表示删除内存中的数据<br/>
	 * @return
	 */
	public OperateType operateType() default OperateType.GET;
	
	/**
	 * 缓存时间，默认1刻钟
	 * 单位毫秒
	 * @return
	 */
	public long ttl() default QUANTER;
	
	/**
	 * 是否忽略空
	 * 默认null的数据也做缓存处理
	 * @return
	 */
	public boolean ignoreNull() default false;
	
	public enum OperateType{
		GET,UPDATE,DELETE
	}
	
}
