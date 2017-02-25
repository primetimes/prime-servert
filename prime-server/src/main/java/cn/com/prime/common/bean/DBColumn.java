package cn.com.prime.common.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对应DB字段
 * @author Rain
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DBColumn {
	
	/**
	 * 数据库表字段名称,注意是驼峰后的格式
	 * @return
	 */
	public String column();
	
	/**
	 * 转换格式，日期填写java中的格式即可
	 * @return
	 */
	public String format() default "";
	
	
}
