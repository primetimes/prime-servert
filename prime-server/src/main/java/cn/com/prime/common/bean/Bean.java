package cn.com.prime.common.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Bean<T> {
	
	private Class<T> clazz;
	
	//DBColumnName
	private Map<String,Property> dbProperites = new HashMap<String,Property>();
	
	private Map<String,Property> pojoProperites = new HashMap<String,Property>();
	
	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
		fillDbProperties(this.clazz);
	}
	
	public Class<T> getClazz() {
		return clazz;
	}
	
	public Map<String, Property> getDbProperites() {
		return dbProperites;
	}
	
	/**
	 * 装载clazz的对应数据库属性
	 */
	private void fillDbProperties(Class cls){
		
		Field[] fs = cls.getDeclaredFields();
		for(Field f:fs){
			Property p = new Property(f);
			dbProperites.put(p.getDbColumn(),p);
			pojoProperites.put(p.getName(),p);
		}
		if(cls.getSuperclass()!=null){
			fillDbProperties(cls.getSuperclass());
		}
		
		
	}
	
	public Property getDBProperty(String name){
		return this.dbProperites.get(name);
	}

}
