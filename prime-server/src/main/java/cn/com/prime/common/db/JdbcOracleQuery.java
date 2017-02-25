package cn.com.prime.common.db;

import java.util.List;
import java.util.Map;

import cn.com.prime.common.support.PartialCollection;

/**
 * 仿照Hibernate中query，目标是代码结构查询简单，方便易用
 * 1.简化sql条件拼接
 * 2.封装查询
 * @author Rain
 *
 */
public class JdbcOracleQuery extends JdbcQuery {

	public JdbcOracleQuery() {
	}

	public JdbcOracleQuery(String dbAlias, String startSql) {
		super(dbAlias, startSql);
	}

	/**
	 * 执行分页查询
	 */
	@Override
	public PartialCollection listPartial() {

		long start = System.currentTimeMillis();
		String finalSql = this.parseSql();
		String countSql = "select count(1) C from (" + finalSql + ")";

		Map countMap = null;
		try {
			countMap = jdbcDao.findUniqueResultAndCloseStmt(countSql, args.toArray(), null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		int total = Integer.valueOf((String) countMap.get("c"));
		if (countMap == null || countMap.size() == 0 || total == 0 || (queryInfo != null && 0 == queryInfo.getLimit())) {
			return new PartialCollection(null, total, this.queryInfo.getOffset());
		}
		finalSql = finalSql + orderBy.toString();
		String querySql = "select * from (select * from (select row_.*, rownum rownum_ from (" + finalSql
				+ ") row_  ) a where Upper##% )where Lower##% ";
		if (queryInfo.getOffset() == null && queryInfo.getLimit() == null) {
			querySql = "select row_.*, rownum rownum_ from (" + querySql + ") row_";
		} else if (queryInfo.getOffset() != null && queryInfo.getLimit() == null) {
			int initIndx = queryInfo.getOffset() == null ? 0 : queryInfo.getOffset().intValue();
			querySql = querySql.replace("Upper##%", "1=1");
			querySql = querySql.replace("Lower##%", "rownum_ >" + initIndx);
		} else if (queryInfo.getOffset() == null && queryInfo.getLimit() != null) {
			int limit = queryInfo.getLimit() == null ? 0 : queryInfo.getLimit().intValue();
			querySql = querySql.replace("Upper##%", "a.rownum_ <=" + limit);
			querySql = querySql.replace("Lower##%", "rownum_ > 0");
		} else {
			int initIndx = queryInfo.getOffset() == null ? 0 : queryInfo.getOffset().intValue();
			int limit = queryInfo.getLimit() == null ? 0 : queryInfo.getLimit().intValue();
			querySql = querySql.replace("Upper##%", "a.rownum_ <=" + (limit + initIndx));
			querySql = querySql.replace("Lower##%", "rownum_ > " + initIndx);
		}
		List<Map> list = jdbcDao.executeQuery(querySql, args.toArray());
		return new PartialCollection(list, total, this.queryInfo.getOffset());

	}

}
