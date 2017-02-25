package cn.com.prime.common.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.prime.common.support.PartialCollection;

/**
 * Mysql查询
 * 继承oracle查询
 * 不同的地方在分页的地方
 * MYSQL直接使用limit 分页
 * @author Rain
 *
 */
public class JdbcMysqlQuery extends JdbcQuery{
	
	/**
	 * 支持三层括号
	 */
	protected static Pattern PATTERN_COUNT = Pattern.compile("\\([^\\(\\)]*(\\([^\\(\\)]*(\\([^\\(\\)]*\\)[^\\(\\)]*)*\\)[^\\(\\)]*)*\\)"); //(\\(([^()]|\\([^()]*\\))*\\)) 
	
	public JdbcMysqlQuery(){
	}
	
	public JdbcMysqlQuery(String dbAlias,String startSql) {
		super(dbAlias, startSql);
	}
	
	protected String getCountSql(String originalSql){
		//log.debug("原sql："+sql);
       	String countSql=originalSql;
		Map map = new HashMap();
		
		Matcher matcher = PATTERN_COUNT.matcher(countSql);		
		while (matcher.find()){
			String key="{flag"+matcher.start()+"}";
			countSql =countSql.replace(matcher.group(),key);
			map.put(key, matcher.group());			
		}
		
		int startIndex=countSql.indexOf(" from ");
		countSql = "select count(1) c "+countSql.substring(startIndex);
		Iterator it = map.keySet().iterator();
		while (it.hasNext()){
			String key = (String)it.next();
			countSql = countSql.replace(key, (String)map.get(key));
		}
		logger.debug("countSql："+countSql);
		return countSql;
	}
	
	/**
	 * 分页查询
	 * @return
	 */
	@Override
	public PartialCollection listPartial() {
		
		String finalSql = this.parseSql();
		String countSql = this.getCountSql(finalSql);//"select count(1) C from (" + finalSql + ")";
		Map countMap = null;
		try {
			countMap = jdbcDao.findUniqueResultAndCloseStmt(countSql, args.toArray(), null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		if(countMap == null || (countMap != null && countMap.isEmpty())){
			return new PartialCollection(new ArrayList(),0,this.queryInfo.getOffset());
		}
		int total = Integer.valueOf((String)countMap.get("c"));
		if(total==0){
			return new PartialCollection(new ArrayList(),0,this.queryInfo.getOffset());
		}
		finalSql = finalSql+orderBy.toString();
		String querySql = finalSql+" limit "+queryInfo.getOffset()+","+queryInfo.getLimit();
		List<Map> list = jdbcDao.executeQuery(querySql, args.toArray());
		return new PartialCollection(list,total,this.queryInfo.getOffset());
		
	}
	
	@Override
	public Query setDateString(String key, String date) {
        //setDateString(key, date, "yyyy-MM-dd hh24:mi:ss");
        //return setDate(key,DateUtil.getDate(date));
        //return this;
        return this.setDateString(key, date, "%Y-%m-%d %H:%i:%s");
    }
	
	@Override
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
        //      finalSql = finalSql+orderBy;

        Matcher set = PATTERN_SET.matcher(finalSql);
        while (set.find()) {
            String conds = set.group();
            String key = set.group(2);
            Object value = this.paramsMap.get(key);
            String replace = "?";
            if (dateMap.get(key) != null) {
                replace = "str_to_date(?,'" + dateMap.get(key) + "')";
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
                replace = "str_to_date(?,'" + dateMap.get(key) + "')";
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
            conds = conds.replaceAll("\\s*where\\s+", "").replaceAll("\\s*(and|or)\\s*\\(", "");
            //System.out.println("1nm conds="+conds+";key="+key);
            Object value = this.paramsMap.get(key);
            if (value == null || "".equals(value)) {
                finalSql = finalSql.replace(conds, "");
            } else {
                String replace = " ? ";
                if (dateMap.get(key) != null) {
                    replace = "str_to_date(?,'" + dateMap.get(key) + "')";
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
        finalSql = finalSql.replaceAll("where\\s+\\)", " )").replaceAll("where\\s+and", " where ")
                .replaceAll("where\\s*$", "").replaceAll("where\\s+order\\s+", " order ")
                .replaceAll("(and|or)\\s+\\(\\s*\\)\\s*", " ").replaceAll("\\s+\\(\\s+(and|or)\\s+", " ( ")
                .replaceAll("where\\s+group\\s", " group ");
//        logger.debug("====" + finalSql + ";args=" + args);

        finalSql = replaceTableDBName(finalSql);

        return finalSql;
    }
	
}
