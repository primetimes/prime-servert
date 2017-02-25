package cn.com.prime.common.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.prime.common.support.Constants;

public class DBNameReplace {

	private static final Logger logger = LoggerFactory.getLogger(DBNameReplace.class);

	/**
	 * 数据库连接配置表
	 */
	private static Map<String, Map> DB_CONN_CONFIG = new HashMap<String, Map>();

	/**
	 * 相关子查询正则表达式
	 */
	private final static Pattern subsqlPattern = Pattern.compile("\\((select[^()]+)\\)(\\s+(?!where)\\w+)?");

	/**
	 * 常见SQL语句正则表达式
	 */
	private final static Pattern sqlPattern = Pattern
			.compile("\\s+from\\s+(?!dual|select)[\\w\\s,]+(?=where|left|union|$)" // 查询语句
					+ "|from\\s+(?!left|dual|select)\\w+\\s+"
					+ "|left\\s+join\\s+\\w+\\s+"
					+ "|select\\s+\\w+\\.nextval(?=\\s+)" // 递增序列语句
					+ "|insert\\s+into\\s+\\w+\\s*" // 插入语句
					+ "|update\\s+\\w+\\s+" // 更新语句
					+ "|merge\\s+into\\s+\\w+\\s+" // merge语句
					+ "|call\\s+\\w+\\s*(?=\\()" // 存储过程语句
					+ "|create\\s+table\\s+\\w+\\s*(?=\\()" // 建表语句
					+ "|create\\s+index\\s+\\w+\\s+on\\s+\\w+\\s+"); // 建索引语句

	/**
	 * 提取SQL中数据库对象名的正则表达式
	 */
	private final static Pattern objectPattern = Pattern
			.compile("(?<=(?:from|into|update|join|table|select|call)\\s+)(?!on|left|where|set)\\w+(?=\\s*|\\.nextval)|(?<=,)\\s*\\w+(?=\\s*)");

	static {
		initConfig();
	}

	/**
	 * 初始化配置
	 */
	public static void initConfig() {
		DB_CONN_CONFIG.clear();
		/*GlobalManager manager = ManagerProxy.getInstance(GlobalManager.class);
		List<Map> list = manager.getDBConnConfig();
		for (Map m : list) {
			DB_CONN_CONFIG.put((String) m.get("dbUserAlias"), m);
		}*/
		logger.debug("DB_CONN_CONFIG=" + DB_CONN_CONFIG);
	}

	/**
	 * 获取DB用户
	 * 
	 * @param dbUserAlias
	 * @return
	 */
	public static String getDBConnName(String dbUserAlias) {
		Map connMap = DB_CONN_CONFIG.get(dbUserAlias);
		if (connMap == null || connMap.size() == 0) {
			return null;
		}
		return (String) connMap.get("dbConnAlias");
	}

	/**
	 * 取数据库用户名
	 * 
	 * @param dbUserAlias
	 * @return
	 */
	public static String getDBUserName(String dbUserAlias) {
		return (String) DB_CONN_CONFIG.get(dbUserAlias).get("dbUserName");
	}

	/**
	 * 是否需要替换
	 * 
	 * @param dbUserAlias
	 * @return
	 */
	public static boolean ifReplace(String dbUserAlias) {
		Map connMap = DB_CONN_CONFIG.get(dbUserAlias);
		if (connMap != null && connMap.size() > 0) {
			if (!Constants.YES.equals(connMap.get("isSame"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 替换SQL中的数据库对象名(表名，序列名，存储过程名，package名),对象名前添加数据库别名，<br/>
	 * 如：im_sp_info 替换成 ： im_zx.im_sp_info <br/>
	 * imessageid.nextval 替换成 im_zx.imessageid.nextval
	 * 
	 * @param sql
	 *            源SQL
	 * @param dbName
	 *            数据库用户名
	 * @return
	 */
	public static String replaceDBName(String sql, String dbUserAlias) {
		long start = System.currentTimeMillis();
		Set<String> tables = getSqlDBObject(sql);
		if (tables != null) {
			for (String table : tables) {
				String replacement = dbUserAlias + "." + table;
				sql = sql.replaceAll(table, replacement);
			}
		}
		long end = System.currentTimeMillis();
		logger.info("replace db object name cost time ：{} ms", (end - start));
		return sql;
	}

	public static void main(String[] args) {
		String sql = "select * from a,b,c";
		sql = sql.replace("${spId}", "1010111");
		sql = sql.replaceAll("\\s{2,}", " ");
		logger.info(replaceDBName(sql, "im_zx"));
	}

	/**
	 * 提取SQL中的所有数据库对象名
	 * 
	 * @param sql
	 * @return
	 */
	public static Set<String> getSqlDBObject(String sql) {
		List<String> subSqls = getSubSql(sql);
		Set<String> result = null;
		if (subSqls != null) {
			result = new HashSet<String>();
			for (String subSql : subSqls) {
				List<String> tables = procSubSql(subSql);
				if (tables != null) {
					for (String table : tables) {
						boolean valid = true;
						Iterator<String> it = result.iterator();
						while (it.hasNext()) {
							String existTable = it.next();
							if (existTable.equals(table)) {
								valid = false;
								break;
							}
							if (existTable.startsWith(table)) {
								it.remove();
								break;
							}
							if (table.startsWith(existTable)) {
								valid = false;
								break;
							}
						}
						if (valid)
							result.add(table);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 子SQL语句中提取表名
	 * 
	 * @param sql
	 *            子SQL语句
	 * @return
	 */
	private static List<String> procSubSql(String sql) {
		if (sql == null | "".equals(sql))
			return null;
		long start = System.currentTimeMillis();
		List<String> tables = new ArrayList<String>();
		Matcher m = sqlPattern.matcher(sql);
		while (m.find()) {
			String subSql = new String(m.group(0));
			Matcher tm = objectPattern.matcher(subSql);
			while (tm.find()) {
				String table = new String(tm.group(0));
				table = table.trim();
				tables.add(table);
			}
		}
		logger.debug("耗时：" + (System.currentTimeMillis() - start) + " ms");
		return tables;
	}

	/**
	 * 拆分SQL语句，提取SQL中的相关子查询语句
	 * 
	 * @param sql
	 *            源SQL
	 * @return
	 */
	public static List<String> getSubSql(String sql) {
		if (sql == null || "".equals(sql))
			return null;
		List<String> subSqls = new ArrayList<String>();
		Matcher m = subsqlPattern.matcher(sql);
		while (m.find()) {
			String group = new String(m.group());
			String subSql = new String(m.group(1));
			logger.debug("相关子查询：" + subSql);
			subSqls.add(subSql);
			sql = sql.replace(group, "");
			sql = sql.replaceAll("from\\s+select", " ");
		}
		subSqls.add(sql);
		return subSqls;
	}
}
