package cn.com.prime.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public abstract class DateUtil {

	public static final String defaultFormat = "yyyy-MM-dd HH:mm:ss";

	public static final int fields[] = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY,
			Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND };

	/**
	 * Date类型转换为Calendar类型
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static Calendar toCalendar(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 设置日期
	 * 
	 * @param date
	 *            源日期
	 * @param field
	 *            日历域
	 * @param value
	 *            值
	 * @return 设置后的日期
	 */
	public static Date set(final Date date, int field, int value) {
		final Date setDate = date == null ? new Date() : date;
		final Calendar calendar = toCalendar(setDate);
		calendar.set(field, value);
		return calendar.getTime();
	}

	/**
	 * 设置日期毫秒数
	 * 
	 * @param date
	 *            日期
	 * @param value
	 *            毫秒数
	 * @return 设置后的日期
	 */
	public static Date setMilliseconds(final Date date, int value) {
		return set(date, Calendar.MILLISECOND, value);
	}

	/**
	 * 设置日期秒数
	 * 
	 * @param date
	 *            日期
	 * @param value
	 *            毫秒数
	 * @return 设置后的日期
	 */
	public static Date setSeconds(final Date date, int value) {
		return set(date, Calendar.SECOND, value);
	}

	/**
	 * 设置日期的分钟数
	 * 
	 * @param date
	 *            日期
	 * @param value
	 *            分钟数
	 * @return 设置后的日期
	 */
	public static Date setMinutes(final Date date, int value) {
		return set(date, Calendar.MINUTE, value);
	}

	/**
	 * 设置日期的小时数
	 * 
	 * @param date
	 *            日期
	 * @param value
	 *            小时数
	 * @return 设置后的日期
	 */
	public static Date setHours(final Date date, int value) {
		return set(date, Calendar.HOUR_OF_DAY, value);
	}

	/**
	 * 设置日期的月份中的天数
	 * 
	 * @param date
	 *            日期
	 * @param value
	 *            天数
	 * @return 设置后的日期
	 */
	public static Date setDays(final Date date, int value) {
		return set(date, Calendar.DAY_OF_MONTH, value);
	}

	/**
	 * 设置日期的月份
	 * 
	 * @param date
	 *            日期
	 * @param value
	 *            月份数
	 * @return 设置后的日期
	 */
	public static Date setMonths(final Date date, int value) {
		return set(date, Calendar.MONTH, value);
	}

	/**
	 * 设置日期的年份
	 * 
	 * @param date
	 *            日期
	 * @param value
	 *            年份
	 * @return 设置后的日期
	 */
	public static Date setYears(final Date date, int value) {
		return set(date, Calendar.YEAR, value);
	}

	/**
	 * 截取日期
	 * 
	 * @param date
	 *            日期
	 * @param filed
	 *            截取的域
	 * @return 截取后的日期
	 */
	public static Date trunc(final Date date, final int field) {
		Date result = date;
		for (int _field : fields) {
			if (_field >= field) {
				switch (_field) {
				case Calendar.YEAR:
					result = setYears(result, 1970);
					break;
				case Calendar.DAY_OF_MONTH:
					result = setDays(result, 1);
					break;
				default:
					result = set(result, _field, 0);
				}
			}
		}
		return result;
	}

	/**
	 * 使用一组格式化规则，将字符串转换为日期
	 * 
	 * @param text
	 *            待转换为日期类型的字符串
	 * @param patterns
	 *            转换的格式数组，为空则以默认格式转换
	 * @return 日期对象
	 */
	public static Date parseDate(String text, String... patterns) {
		if (text == null || "".equals(text))
			return null;
		if (patterns == null || patterns.length == 0)
			patterns = new String[] { defaultFormat };
		Date result = null;
		for (String pattern : patterns) {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			try {
				result = format.parse(text);
				return result;
			} catch (Exception e) {
				result = null;
			}
		}
		return null;
	}
	
	public static Date parseDateByPattern(String text, String pattern) {
		if (pattern == null)
			pattern = defaultFormat;
		
		return parseDate(text,defaultFormat);
	}

	/**
	 * 将源日期增加一定量，如加一分钟，加一天，加一个月等等
	 * 
	 * @param date
	 *            源日期
	 * @param field
	 *            日历类（Calendar）定义的域，如：Calendar.SENCOND,Calendar.MINUTES
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date add(final Date date, final int field, final int amount) {
		final Date addDate = date == null ? new Date() : date;
		final Calendar calendar = toCalendar(addDate);
		calendar.add(field, amount);
		return calendar.getTime();
	}

	/**
	 * 日期加（减）amount毫秒
	 * 
	 * @param date
	 *            源日期
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date addMilliseconds(final Date date, final int amount) {
		return add(date, Calendar.MILLISECOND, amount);
	}

	/**
	 * 日期加（减）amount秒
	 * 
	 * @param date
	 *            源日期
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date addSeconds(final Date date, final int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	/**
	 * 日期加（减）amount分钟
	 * 
	 * @param date
	 *            源日期
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date addMinutes(final Date date, final int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	/**
	 * 日期加（减）amount小时
	 * 
	 * @param date
	 *            源日期
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date addHours(final Date date, final int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	/**
	 * 日期加（减）amount天
	 * 
	 * @param date
	 *            源日期
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date addDays(final Date date, final int amount) {
		return add(date, Calendar.DAY_OF_MONTH, amount);
	}

	/**
	 * 日期加（减）amount月
	 * 
	 * @param date
	 *            源日期
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date addMonths(final Date date, final int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	/**
	 * 日期加（减）amount年
	 * 
	 * @param date
	 *            源日期
	 * @param amount
	 *            增加的数量，可以为负
	 * @return 增加后的日期
	 */
	public static Date addYears(final Date date, final int amount) {
		return add(date, Calendar.YEAR, amount);
	}

	/**
	 * 格式化日期，使用默认格式
	 * 
	 * @param date
	 *            日期
	 * @return 日期字符串
	 */
	public static String fmtDate(Date date) {
		return fmtDate(date, defaultFormat);
	}

	/**
	 * 格式化日期得到字符串
	 * 
	 * @param date
	 *            日期
	 * @param format
	 *            格式
	 * @return 日期字符串
	 */
	public static String fmtDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 解析字符串得到日期对象
	 * 
	 * @param text
	 *            日期字符串
	 * @param format
	 *            格式
	 * @return 日期对象
	 */
	public static Date getDate(String text, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 使用默认格式，解析字符串得到日期对象
	 * 
	 * @param text
	 *            字符串
	 * @return 日期对象
	 */
	public static Date getDate(String text) {
		if(StringUtil.isNullOrEmpty(text)){
			return null;
		}
		String format = defaultFormat;
		if (text != null && text.length() == 10) {
			format = "yyyy-MM-dd";
		}
		return getDate(text, format);
	}

	/**
	 * 根据毫秒数获取时间
	 * @param time
	 * @return
	 */
	public static Date getDate(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar.getTime();
	}

	/**
	 * 查询月份中第index天，从index从1开始
	 * 
	 * @param month
	 *            月份字符串
	 * @param index
	 *            索引
	 * @return 月份第index天
	 */
	public static Date indexOfMonth(String month, int index) {
		Date date = parseDate(month, "yyyy-MM", "yyyy/MM", "yyyyMM");
		if (date == null)
			return null;
		return indexOfMonth(date, index);
	}

	/**
	 * 查询月份中第index天，从index从1开始
	 * 
	 * @param date
	 *            月份日期
	 * @param index
	 *            月份中天数索引
	 * @return 月份第index天
	 */
	public static Date indexOfMonth(Date date, int index) {
		Date month = date == null ? new Date() : date;
		month = trunc(date, Calendar.DATE);
		Calendar cal = toCalendar(month);
		int maxIndex = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (index < 0 || index > maxIndex)
			return null;
		return setDays(month, index);
	}

	/**
	 * 查询月份中倒数第index天，从index从1开始
	 * 
	 * @param month
	 *            月份字符串
	 * @param index
	 *            倒数索引
	 * @return 月份倒数第index天
	 */
	public static Date lastIndexOfMonth(String month, int index) {
		Date date = parseDate(month, "yyyy-MM", "yyyy/MM", "yyyyMM");
		if (date == null)
			return null;
		return lastIndexOfMonth(date, index);
	}

	/**
	 * 查询月份中倒数第index天，从index从1开始
	 * 
	 * @param month
	 *            月份日期
	 * @param index
	 *            倒数索引
	 * @return 月份倒数第index天
	 */
	public static Date lastIndexOfMonth(Date date, int index) {
		Date month = date == null ? new Date() : date;
		month = trunc(date, Calendar.DATE);
		Calendar cal = toCalendar(month);
		int maxIndex = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		if (index < 0 || index > maxIndex)
			return null;
		return setDays(month, maxIndex - index + 1);
	}

	/**
	 * 遍历后或前amount天之间的所有日期
	 * 
	 * @param amount
	 * @return
	 */
	public static List<Date> iteratorDays(int amount) {
		List<Date> result = new ArrayList<Date>();
		Date today = new Date();
		int start = amount >= 0 ? 1 : amount;
		int end = amount >= 0 ? amount + 1 : 0;
		for (int i = start; i < end; i++) {
			Date date = addDays(today, i);
			result.add(date);
		}
		return result;
	}

	/**
	 * 返回开始日期和结束日期之间的所有日期
	 * 
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static List<String> iteratorDays(String beginDate, String endDate) {
		List<String> result = new ArrayList<String>();
		Date begin = getDate(beginDate, "yyyy-MM-dd");
		Date end = getDate(endDate, "yyyy-MM-dd");

		if (begin.after(end))
			return null;
		for (Date date = begin; date.before(end); date = addDays(date, 1)) {
			String str = fmtDate(date, "yyyy-MM-dd");
			result.add(str);
		}
		result.add(endDate);
		return result;
	}

	public static java.sql.Date getSQLDate(String text, String format) {
		if (text == null || "".equals(text)) {
			return null;
		}
		long time = getDate(text, format).getTime();
		return new java.sql.Date(time);
	}

	public static java.sql.Timestamp getTimestamp(Date date) {
		return new java.sql.Timestamp(date.getTime());
	}

	public static String getCurDateField(String format) {
		Date date = new Date();
		return fmtDate(date, format);
	}

	/**
	 * 获取给定月第1天
	 * 验证日期格式
	 * @param month 'yyyy-MM'
	 * @return 'yyyy-MM-dd'
	 */
	public static String getMonthFirstDay(String month) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date theDate;
		try {
			theDate = df.parse(month);
			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(theDate);
			gcLast.set(Calendar.DAY_OF_MONTH, 1);
			return df1.format(gcLast.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 获取下月第1天
	 * 
	 * @return
	 */
	public static Date getNextMonthFirstDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	/**
     * 获取给定月份下月的第1天
     * 
     * @return
     */
    public static String getNextMonthFirstDay(String month) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(df.parse(month));
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            return df1.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/**
	 * 获取下月最后一天
	 * 
	 * @return
	 */
	public static Date getNextMonthLastDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	/**
	 * 获取当月最后一天'yyyy-MM'
	 * 
	 * @param date
	 * @return 'yyyy-MM-dd'
	 * @throws ParseException
	 */
	public static String getMonthLastDay(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date theDate;
		try {
			theDate = df.parse(date);
			GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
			gcLast.setTime(theDate);
			gcLast.set(Calendar.DAY_OF_MONTH, 1);
			gcLast.roll(Calendar.DAY_OF_MONTH, -1);
			return df1.format(gcLast.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取给定月份的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthLastDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}

	/**
	 * 获取指定月前一月最后一天'yyyy-MM'
	 * 
	 * @param date
	 * @return 'yyyy-MM-dd'
	 * @throws ParseException
	 */
	public static String getLastMonthMaxDay(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date theDate = null;
		try {
			theDate = df.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.add(Calendar.MONTH, -1);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		gcLast.roll(Calendar.DAY_OF_MONTH, -1);
		return df1.format(gcLast.getTime());
	}

	/**
	 * 获取指定月倒数第二天'yyyy-MM'
	 * 
	 * @param date
	 * @return 'yyyy-MM-dd'
	 * @throws ParseException
	 */
	public static String getMonthLastSecondDay(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date theDate = null;
		try {
			theDate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		gcLast.roll(Calendar.DAY_OF_MONTH, -1);
		gcLast.add(Calendar.DAY_OF_MONTH, -1);
		return df1.format(gcLast.getTime());
	}
	
	public static String compareCurrentTime(String date){
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date cur = new Date();
		try {
			Date d = df1.parse(date);
			if(d.getTime() < cur.getTime()){
				return date;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return date;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(cur);
//		cal.add(Calendar.MINUTE, -30);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String yesterday = df1.format(cal.getTime());
		return yesterday;
	}
	
	public static String getYesterday(){
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		Date cur = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(cur);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		cur = cal.getTime();
		return df1.format(cur);
	}
	
	/**
	 * 验证时间是否符合格式
	 * @param format 格式
	 * @param times  时间数组
	 * @return
	 */
	public static boolean timeNotValid(String format, String... times){
        if(null == times||times.length == 0){
            return false;
        }
        
        for (String time : times) {
            if(StringUtil.isNullOrEmpty(getDate(time,format))){
                return true;
            }
        }
        return false;
    }
}
