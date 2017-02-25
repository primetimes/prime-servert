package cn.com.prime.common.bean;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.com.prime.common.db.DBFieldFmtHepler;
import cn.com.prime.common.util.DateUtil;

public final class Property {
	
	static Map<String,Class<?>> baseType = new HashMap<String,Class<?>>();
	
	static{
		baseType.put("long", Long.class);
		baseType.put("int", Integer.class);
		baseType.put("double", Double.class);
		baseType.put("float", Float.class);
		baseType.put("char", Character.class);
		baseType.put("short", Short.class);
		baseType.put("byte", Byte.class);
		baseType.put("boolean", Boolean.class);
		
	}
	
	
	protected Logger logger = Logger.getLogger(getClass());
	
	public Property() {
		
	}
	
	public Property(Field f) {
		setField(f);
	}
	
	private Field field;
	
	/**
	 * 字段名称
	 */
	private String name;
	
	/**
	 * 字段返回类型
	 */
	private Class<?> type;
	
	/**
	 * 数据格式
	 */
	private String valueFormat;
	
	/**
	 * 数据字段名称
	 */
	private String dbColumn;
	
	public Class<?> getObjectType(Class<?> c) {
		Class<?> cc = baseType.get(c.getName());
		if(cc == null){
			cc = c;
		}
		return cc;
	}
	
	
	public String getValueFormat() {
		return valueFormat;
	}

	public void setValueFormat(String valueFormat) {
		this.valueFormat = valueFormat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}

	public String getDbColumn() {
		return dbColumn;
	}

	public void setDbColumn(String dbColumn) {
		this.dbColumn = dbColumn;
	}
	
	/**
	 * 赋值Field
	 * @param f
	 */
	public void setField(Field f){
		this.field = f;
		this.field.setAccessible(true);
		this.name = f.getName();
		this.type = getObjectType(f.getType());
		DBColumn dbc = f.getAnnotation(DBColumn.class);
		if(dbc==null || dbc.column() == null){
			this.setDbColumn(this.name);
			return;
		}
		String columnName = DBFieldFmtHepler.changeColumnToFieldName(dbc.column());
		this.setDbColumn(columnName);
	}
	
	/**
	 * 获取对象属性的值
	 * @param value
	 * @return
	 */
	public Object getPropertyValue(Object value){
		if(value == null){
			return null;
		}
		try {
			return this.field.get(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 给当前字段赋值
	 * 这里value目前只能输入String类型的，后期扩展
	 * @param obj
	 * @param value
	 */
	private void setPropertyByType(Object obj,Object value) throws Exception{
		if(value == null){
			this.field.set(obj, null);
			return;
		}
		if(this.type.getName().equals(Date.class.getName())){
			this.field.set(obj, DateUtil.parseDateByPattern(value+"", this.getValueFormat()));
		}else if(this.type.getName().startsWith("java.lang.")){
			Object args[] = new Object[1];
			args[0] = value+"";
			Object v = type.getConstructor(String.class).newInstance(args);
			this.field.set(obj,v);
		}
	}
	
	/**
	 * 给当前字段赋值
	 * 这里value目前只能输入String类型的，后期扩展
	 * @param obj
	 * @param value
	 */
	public void setPropertyValue(Object obj,Object value){
		try {
			this.setPropertyByType(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("赋值失败",e);
		}
	}
	
}
