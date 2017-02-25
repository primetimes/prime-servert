package cn.com.prime.common.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.com.prime.common.support.PartialCollection;
import cn.com.prime.common.support.QueryInfo;

/**
 * 数据库Query
 * 提供查询、删除、插入、更新等一系列操作数据库方法
 * @author Rain
 *
 */
public interface Query {
	
	/**
	 * 查询数据，返回List
	 * @return
	 */
	public List<?> list();
	
	/**
	 * 查询返回pojo Bean
	 * @param clazz
	 * @return
	 */
	public <T> List<T> list(Class<T> clazz);
	
	/**
	 * 查询带有分页的数据
	 * @return
	 */
	public PartialCollection listPartial();
	
	
	/**
	 * 配置数据库别名
	 * @param dbAlias
	 * @return
	 */
	public Query setDBAlias(String dbAlias);
	
	/**
	 * 设置数据库名称
	 * @param dbAlias
	 * @return
	 */
	public Query setDbUserName(String dbUserName);
	
	/**
	 * 添加sql语句
	 * @param startSql
	 * @return
	 */
	public Query appendSql(String startSql);
	
	/**
	 * 设置条件的值，如果为空，sql中的动态条件会被替换
	 * @param key
	 * @param value
	 * @return
	 */
	public Query setParameter(String key,Object value);
	
	/**
	 * 设置条件的值，如果值为空，会抛出Runtime异常
	 * @param key 对应条件
	 * @param value 条件的值
	 * @param required 是否必须
	 * @return
	 */
	public Query setParameter(String key,Object value,boolean required);
	
	/**
	 * 设置like的值
	 * @param key
	 * @param value
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public Query setLike(String key,String value,String prefix,String suffix);
	
	/**
	 * 设置分页信息
	 * @param queryInfo
	 * @return
	 */
	public Query setQueryInfo(QueryInfo queryInfo);
	
	/**
	 * 设置字符日期，并可自定义格式。
	 * @param key
	 * @param date
	 * @param dateFormat 数据库日期格式
	 * @return
	 */
	public Query setDateString(String key,String date,String dateFormat);
	
	/**
	 * 设置字符日期，并可自定义格式。
	 * @param key
	 * @param date
	 * @param dateFormat 数据库日期格式
	 * @return
	 */
	public Query setDateString(String key,String date,String dateFormat,boolean required);
	
	/**
	 * 设置时间
	 * @param key
	 * @param date
	 * @return
	 */
	public Query setDate(String key,Date date);
	
	/**
	 * 设置字符日期，格式采用默认值yyyy-MM-dd hh24:mi:ss
	 * @param key
	 * @param date
	 * @return
	 */
	public Query setDateString(String key,String date);
	
	/**
	 * 设置In值
	 * @param key 
	 * @param required 是否为必须
	 * @param values 值
	 * @return
	 */
	public Query setIn(String key,boolean required,String ...values);
	
	/**
	 * 设置In值
	 * @param key
	 * @param values
	 * @return
	 */
	public Query setIn(String key,String ...values);
	
	/**
	 * 设置order by
	 * @param columnName
	 * @param order
	 * @return
	 */
	public Query setOrderBy(String columnName,String order);
	
	/**
	 * 执行更新操作，注意只能动态设置条件
	 */
	public void executeUpdate();
	
	/**
	 * 获取结果唯一
	 * @return
	 */
	public Map<String,String> uniqueResult();
	
	/**
	 * 返回一个具体类型的值
	 * @param clazz
	 * @return
	 */
	public <T> T uniqueResult(Class<T> clazz);
	
	/**
	 * 执行更新操作，条件已知
	 * @param args
	 */
	public void executeUpdate(Object args[]);
	
	/**
	 * 插入一条数据
	 * @param insertMap
	 */
	public void insert(String table,Map<String,Object> insertMap);
	
	/**
	 * 插入一个对象Bean
	 * @param table
	 * @param obj
	 */
	public void insert(String table,Object obj);
	
	
	/**
	 * 查询列的信息
	 * @return
	 */
	public Map<String,Map> executeColumnQuery();

	public Long insertReturnId(String table, Map<String, Object> insertMap);

	
}
