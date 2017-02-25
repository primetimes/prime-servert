package cn.com.prime.common.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanFactory implements ApplicationContextAware {
	
	private static ApplicationContext context;
	
	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
	
	public static Object getBean(String beanName){
		
		Object o=context.getBean(beanName);
		
		return o;
	}

}
