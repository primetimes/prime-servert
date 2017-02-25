package cn.com.prime.common.db;

import java.sql.DriverManager;
import java.util.Enumeration;


/**
 * 数据库Query生产
 * 目前一个项目中只支持配置一种数据库
 * @author Rain
 * 
 */
public class QueryFactory {
	
	//数据库类型
	protected static DBType dbType = DBType.MYSQL;
	
	static{
		if(dbType == null){
			Enumeration e = DriverManager.getDrivers();
			while(e.hasMoreElements()){
				Object o = e.nextElement();
				if(o.getClass().getName().toLowerCase().contains("oracle")){
					dbType = DBType.ORACLE;
				}else if(o.getClass().getName().toLowerCase().contains("mysql")){
					dbType = DBType.MYSQL;
				}
			}
		}
	}
	
	/**
	 * 产生Query
	 * 更具当前配置的数据库
	 * @return
	 */
	public static Query getJdbcQuery(){
		if(dbType == DBType.ORACLE){
			return new JdbcOracleQuery();
		}else if(dbType == DBType.MYSQL){
			return new JdbcMysqlQuery();
		}
		throw new RuntimeException("不支持配置的数据库类型");
	}
	
}
