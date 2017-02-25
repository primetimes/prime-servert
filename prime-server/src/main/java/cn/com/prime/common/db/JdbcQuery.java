package cn.com.prime.common.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.prime.common.bean.BeanUtil;
import cn.com.prime.common.bean.OrmObject;
import cn.com.prime.common.support.QueryInfo;
import cn.com.prime.common.util.DateUtil;

/**
 * 仿照Hibernate中query，目标是代码结构查询简单，方便易用
 * 1.简化sql条件拼接
 * 2.封装查询
 * @author Rain
 *
 */
public abstract class JdbcQuery implements Query {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected static final Pattern PATTERN_WHERE = Pattern
			.compile("\\s+(where|and|or|start\\s+with)[\\s\\(]+\\S+\\s*([>|=|<]{1,2}|(like)|in|not\\s+in)\\s*:([\\S&&[^\\(\\)]]+)|\\s+(where|and|or)\\s+instr\\s*\\(\\s*\\S+\\s*,\\s*:(\\S+)\\s*\\)\\s*[><=]{1,2}\\s*\\d");
	protected static final Pattern PATTERN_SET = Pattern.compile("(\\s+set|,)\\s*(?:\\w+\\.)?\\w+\\s*=\\s*:(\\w+)");
	protected static final Pattern PATTERN_FUNC = Pattern.compile("(?<=\\(|,)\\s*:(\\w+)");

	//private static final Pattern PATTERN_LIKE = Pattern.compile("\\s+(where|and|or)\\s+(\\S+)\\s*like+\\s*:(\\S+)");
	//\\s*(set|,)\\s*\\S+=\\s*:([\\S&&[^,]]+)
	//private static final Pattern PATTERN_INSTR = Pattern.compile("\\s+(where|and|or)\\s+instr\\s*\\(\\s*\\S+\\s*,\\s*:(\\S+)\\s*\\)\\s*[><=]{1,2}\\s*\\d");
	//private static final Pattern PATTERN_DATE = Pattern.compile("\\s+(where|and|or)\\s+\\S+\\s*[>|=|<]{1,2}\\s*to_date\\s*\\(\\s*:(\\S+)\\s*,\\S+\\)\\s*");

	public static void main(String[] args) {
		String s = "insert into values (:taskId,:spId)";
		JdbcQuery query = new JdbcOracleQuery(null, s);
		query.setParameter("spId", "10110062");
		query.setParameter("taskId", "10110062");
		String sql = query.parseSql();

		System.out.println("sql=" + sql + ";\n args:" + query.args);
	}

	protected CommonDao jdbcDao;
	protected QueryInfo queryInfo;

	//参数
	protected Map paramsMap = new HashMap();
	protected Map dateMap = new HashMap();
	protected Map inMap = new HashMap();

	protected List args = new ArrayList();

	protected List<String> orderByList = new ArrayList();

	protected StringBuffer orderBy = new StringBuffer();

	protected StringBuffer sql = new StringBuffer();

	protected String dbUserName;

	public JdbcQuery() {

	}

	public JdbcQuery(String dbAlias, String startSql) {
		this.jdbcDao = new CommonDao(dbAlias);
		sql.append(startSql);
	}

	public Query setDBAlias(String dbAlias) {
		if (this.jdbcDao == null) {
			this.jdbcDao = new CommonDao(dbAlias);
		} else {
			this.jdbcDao.setDbAlias(dbAlias);
		}

		return this;
	}

	public Query appendSql(String startSql) {
		sql.append(startSql);
		return this;
	}

	public Query setQueryInfo(QueryInfo queryInfo) {
		this.queryInfo = queryInfo;
		return this;
	}

	public Query setParameter(String key, Object value) {
		this.setParameter(key, value, false);
		return this;
	}

	public Query setParameter(String key, Object value, boolean required) {
		if (required && (value == null || "".equals(value))) {
			throw new RuntimeException("sql parameter:" + key + " is null");
		}
		this.paramsMap.put(key, value);
		return this;
	}

	public Query setIn(String key, String... values) {
		return setIn(key, false, values);
	}

	public Query setIn(String key, boolean required, String... values) {
		if (values == null || values.length == 0) {
			if(required){
				throw new RuntimeException("sql parameter:"+key+" is null");
			}
			return this;
		}
		List inList = new ArrayList(values.length);
		for (String value : values) {
			if (value == null || "".equals(value)) {
				continue;
			}
			inList.add(value);
		}
		if (inList.size() > 0) {
			//this.paramsMap.put(key,inList);
			inMap.put(key, inList);
			setParameter(key, values, required);
		}
		return this;
	}

	public Query setOrderBy(String columnName, String order) {
		if (order == null) {
			order = "asc";
		}
		orderByList.add(columnName + " " + order);
		return this;
	}

	public Query setLike(String key, String value, String prefix, String suffix) {
		if (value == null || "".equals(value)) {
			return this;
		}
		if (prefix == null)
			prefix = "";
		if (suffix == null)
			suffix = "";
		this.paramsMap.put(key, prefix + value + suffix);
		return this;
	}

	public Query setDateString(String key, String date, String dateFormat) {
		
		if (date != null && !"".equals(date)) {
			//date = "to_date('"+date+"',"+dateFormat+"')";
			dateMap.put(key, dateFormat);
		}
		setParameter(key, date);
		return this;
		//return setDate(key,DateUtil.getDate(date));
	}

	protected void assertRequired(String key,boolean required,Object ...value){
		if(required && (value==null || "".equals(value))){
			throw new RuntimeException("sql parameter:"+key+" is null");
		}
	}
	
	public Query setDateString(String key, String date, String dateFormat, boolean required) {
		
		assertRequired(key,required,date);
		this.setDateString(key,date,dateFormat);
		return this;
		/*if (date != null && !"".equals(date)) {
			//date = "to_date('"+date+"',"+dateFormat+"')";
			//dateMap.put(key, dateFormat);
		} else if (required) {
			throw new RuntimeException("key=" + key + " 不能为空");
		}
		return setDate(key,DateUtil.getDate(date));*/
		/*setParameter(key, date);
		return this;*/
	}

	@Override
	public Query setDate(String key, Date date) {
		if (null != date) {
			return this.setDateString(key, DateUtil.fmtDate(date));
		}
		return this;
		/*java.sql.Timestamp d = null;
		if(date != null){
			d = new java.sql.Timestamp(date.getTime());
		}
		setParameter(key, d);*/
	}

	public Query setDateString(String key, String date) {
		//setDateString(key, date, "yyyy-MM-dd hh24:mi:ss");
		//return setDate(key,DateUtil.getDate(date));
		//return this;
		return this.setDateString(key, date, "yyyy-MM-dd hh24:mi:ss");
	}

	public String parseSql() {
		String finalSql = sql.toString();
		finalSql = finalSql.replaceAll("\\s{2,}", " ");

		for (String order : orderByList) {
			if ("".equals(orderBy.toString())) {
				orderBy.append(" order by ").append(order);
			} else {
				orderBy.append(",").append(order);
			}
		}
		if (orderByList == null || orderByList.size() == 0) {

		}
		//		finalSql = finalSql+orderBy;

		Matcher set = PATTERN_SET.matcher(finalSql);
		while (set.find()) {
			String conds = set.group();
			String key = set.group(2);
			Object value = this.paramsMap.get(key);
			String replace = "?";
			if (dateMap.get(key) != null) {
				replace = "to_date(?,'" + dateMap.get(key) + "')";
			}
			replace = conds.replace(":" + key, replace);
			finalSql = finalSql.replaceAll(conds, replace);
			args.add(value);
		}

		Matcher func = PATTERN_FUNC.matcher(finalSql);
		while (func.find()) {
			String conds = func.group();
			String key = func.group(1);
			Object value = this.paramsMap.get(key);
			String replace = "?";
			if (dateMap.get(key) != null) {
				replace = "to_date(?,'" + dateMap.get(key) + "')";
			}
			replace = conds.replace(":" + key, replace);
			finalSql = finalSql.replaceAll(conds, replace);
			args.add(value);
		}

		Matcher where = PATTERN_WHERE.matcher(finalSql);
		while (where.find()) {
			String conds = where.group();
			String key = null;
			if (where.group(4) != null) {
				key = where.group(4);
			} else if (where.group(6) != null) {
				key = where.group(6);
			} else {
				throw new RuntimeException("parse sql exception,can't find the key");
			}
			//String key = nm.group(2);
			//System.out.println("0nm conds="+conds+";key="+key);
			conds = conds.replaceAll("\\s*where\\s+", "").replaceAll("\\s*(and|or)\\s+\\(", "");
			//System.out.println("1nm conds="+conds+";key="+key);
			Object value = this.paramsMap.get(key);
			if (value == null || "".equals(value)) {
				finalSql = finalSql.replace(conds, "");
			} else {
				String replace = " ? ";
				if (dateMap.get(key) != null) {
					replace = "to_date(?,'" + dateMap.get(key) + "')";
					//value = new java.sql.Date(DateUtil.getDate((String)value).getTime());
				}
				if (inMap.get(key) != null) {
					List<String> inList = (List) inMap.get(key);
					replace = "(";
					for (String in : inList) {
						if (replace.equals("(")) {
							replace = replace + "?";
						} else {
							replace = replace + ",?";
						}
						args.add(in);
					}
					replace = replace + ")";

				} else {
					args.add(value);
				}
				finalSql = finalSql.replaceFirst(":" + key, replace);
			}
		}

		//finalSql = finalSql.toLowerCase();
		finalSql = finalSql.replaceAll("where\\s+\\)", "where ").replaceAll("where\\s+and", " where ")
				.replaceAll("where\\s*$", "").replaceAll("where\\s+order\\s+", " order ")
				.replaceAll("(and|or)\\s+\\(\\s*\\)\\s*", " ").replaceAll("\\s+\\(\\s+(and|or)\\s+", " ( ");
		logger.debug("====" + finalSql + ";args=" + args);

		finalSql = replaceTableDBName(finalSql);

		return finalSql;
	}

	@Override
	public Query setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
		return this;
	}

	/**
	 * 替换数据库物理连接，在各table前面加上用户名前缀
	 * @param sql
	 * @return
	 */
	protected String replaceTableDBName(String sql) {
		if (this.dbUserName != null && !"".equals(this.dbUserName)) {
			sql = DBNameReplace.replaceDBName(sql, dbUserName);
		}
		return sql;
	}
	
	/**
	 * 查询数据转换成指定的clazz
	 */
	@Override
	public <T> List<T> list(Class<T> clazz) {
		List mapList = this.list();
		return BeanUtil.convertMapToBeans(mapList,clazz);
	}
	
	/**
	 * 执行查询
	 */
	@Override
	public List<Map> list() {
		String finalSql = this.parseSql();
		finalSql = finalSql + orderBy.toString();
		List<Map> list = jdbcDao.executeQuery(finalSql, args.toArray());
		return list;
	}

	/**
	 * 获取唯一结果
	 */
	@Override
	public Map<String, String> uniqueResult() {
		List<Map> list = (List<Map>) list();
		if (list != null && list.size() > 0) {
			Map result = list.get(0);
			return result;
		}
		return null;
	}

	@Override
	public <T> T uniqueResult(Class<T> clazz) {
		Map resultMap = uniqueResult();
		if (resultMap != null) {
			if(clazz.isAssignableFrom(OrmObject.class)){
				return (T)BeanUtil.convertMapToBean(resultMap, clazz);
			}
			Set<String> set = resultMap.keySet();
			Object value = null;
			for (String key : set) {
				value = resultMap.get(key);
			}
			if (clazz.getName().equals("java.lang.String")) {
				return (T) (value == null || "".equals(value) ? null : value.toString());
			} else if (clazz.getName().equals("java.lang.Integer")) {
				return (T) new Integer((value == null || "".equals(value) ? "0" : value.toString()));
			} else if (clazz.getName().equals("java.lang.Double")) {
				return (T) new Double((value == null || "".equals(value) ? "0.0" : value.toString()));
			} else if (clazz.getName().equals("java.lang.Long")) {
				return (T) new Long((value == null || "".equals(value) ? "0" : value.toString()));
			}
		}
		return null;
	}

	/**
	 * 执行update
	 */
	@Override
	public void executeUpdate() {
		String finalSql = this.parseSql();
		jdbcDao.executeUpdate(finalSql, args.toArray());
	}

	public void executeUpdate(Object args[]) {
		String finalSql = sql.toString();
		finalSql = replaceTableDBName(finalSql);
		jdbcDao.executeUpdate(finalSql, args);
	}
	
	public Long executeUpdateReturnGenKey(Object args[]) {
		String finalSql = sql.toString();
		finalSql = replaceTableDBName(finalSql);
		return jdbcDao.executeUpdateReturnGenKey(finalSql, args);
	}

	@Override
	public Map<String, Map> executeColumnQuery() {
		String finalSql = sql.toString();
		finalSql = replaceTableDBName(finalSql);
		return jdbcDao.executeColumnQuery(finalSql);
	}
	
	@Override
	public Long insertReturnId(String table, Map<String, Object> insertMap) {
		
		StringBuffer insertSql = new StringBuffer();
		insertSql.append("insert into ").append(table).append("(");
		Set<String> keySet = insertMap.keySet();
		boolean isFirst = true;
		List<Object> args = new ArrayList(insertMap.size());
		StringBuffer valuesSql = new StringBuffer();
		for (String key : keySet) {
			String replace = "?";
			Object value = insertMap.get(key);
			key = DBFieldFmtHepler.changeFieldToColumnName(key);
			if (value != null) {
				if (value instanceof Date) {
					value = new java.sql.Timestamp(((Date) value).getTime());
				}
			}
			if("".equals(value)){
				value = null;
			}
			args.add(value);
			if (isFirst) {
				isFirst = false;
				insertSql.append(key);
				valuesSql.append(replace);
			} else {
				insertSql.append(",").append(key);
				valuesSql.append(",").append(replace);
			}
		}
		insertSql.append(") values (").append(valuesSql).append(")");
		this.sql = insertSql;
		return this.executeUpdateReturnGenKey(args.toArray());
	}
	
	@Override
	public void insert(String table, Map<String, Object> insertMap) {
		
		StringBuffer insertSql = new StringBuffer();
		insertSql.append("insert into ").append(table).append("(");
		Set<String> keySet = insertMap.keySet();
		boolean isFirst = true;
		List<Object> args = new ArrayList(insertMap.size());
		StringBuffer valuesSql = new StringBuffer();
		for (String key : keySet) {
			String replace = "?";
			Object value = insertMap.get(key);
			key = DBFieldFmtHepler.changeFieldToColumnName(key);
			if (value != null) {
				if (value instanceof Date) {
					value = new java.sql.Timestamp(((Date) value).getTime());
				}
			}
			if("".equals(value)){
				value = null;
			}
			args.add(value);
			if (isFirst) {
				isFirst = false;
				insertSql.append(key);
				valuesSql.append(replace);
			} else {
				insertSql.append(",").append(key);
				valuesSql.append(",").append(replace);
			}
		}
		insertSql.append(") values (").append(valuesSql).append(")");
		this.sql = insertSql;
		this.executeUpdate(args.toArray());
	}
	
	@Override
	public void insert(String table, Object obj) {
		Map<String,Object> insertMap = BeanUtil.convertBeanToDBMap(obj);
		this.insert(table, insertMap);
	}

}
