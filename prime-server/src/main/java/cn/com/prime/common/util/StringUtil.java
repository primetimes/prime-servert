package cn.com.prime.common.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 字符串处理工具，生成uuid，过滤空字符串、对象....
 * 
 * @version 1.0.0
 * @since jdk1.6
 * @author flaginfo.com
 * @copyright © 2014, flaginfo Corporation. All rights reserved.
 * @History: 1.0.0 | anqi.feng | 2014-09-12 13:01:05 | initialization
 * 
 */
public class StringUtil {
	
	private static Logger logger = LoggerFactory.getLogger(StringUtil.class);
	private final static int[] li_SecPosValue = { 1601, 1637, 1833, 2078, 2274,  
            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,  
            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590 };  
    private final static String[] lc_FirstLetter = { "a", "b", "c", "d", "e",  
            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",  
            "t", "w", "x", "y", "z" };

	/**
	 * 生成32位随机字符串
	 * 
	 * @return
	 */
	public static String getUUID() {
		UUID uuid = java.util.UUID.randomUUID();
		String uuidStr = uuid.toString().toUpperCase();
		if (isNullOrEmpty(uuidStr)) {
			return "";
		}
		return uuidStr.replace("-", "");
	}

	/**
	 * 判断一个对象或者是字符串是否为空
	 * 
	 * @param obj
	 *            对象或字符串
	 * @return
	 */
	public static boolean isNullOrEmpty(final Object obj) {
		boolean result = false;
		if (obj == null || "null".equals(obj)
				|| "".equals(obj.toString().trim())) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 判断values中的是否有空值？
	 * @param validMap
	 * @param key
	 * @return
	 */
	public static boolean isNullOrEmptyInArray(Object ...values) {
		if(values == null || values.length == 0){
			return true;
		}
		for(Object v:values){
			if(isNullOrEmpty(v)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断values中的是否不全为空？
	 * @param validMap
	 * @param key
	 * @return
	 */
	public static boolean isNotNullOrEmptyInArray(Object ...values) {
		if(values == null || values.length == 0){
			return false;
		}
		for(Object v:values){
			if(isNotNullOrEmpty(v)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断Map中的某几个key是否为空？
	 * @param validMap
	 * @param key
	 * @return
	 */
	public static boolean isNullOrEmptyInMap(Map validMap,Object ...key) {
		if(validMap == null){
			return true;
		}
		if(key == null || key.length == 0) {
			return false;
		}
		
		for(Object k:key){
			if(isNullOrEmpty(validMap.get(k))){
				logger.info("validMap {} key= {} is null",validMap,k);
				return true;
			}
		}
		return false;
	}
	

	/**
	 * 判断一个对象或者是字符串是否为空
	 * 
	 * @param obj
	 *            对象或字符串
	 * @return
	 */
	public static boolean isNotNullOrEmpty(final Object obj) {
		boolean result = false;
		if (obj == null || "null".equals(obj)
				|| "".equals(obj.toString().trim())) {
			result = true;
		}
		return !result;
	}
	
	
	/**
	 * 判断是否为数字字符串
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否超过最大长度
	 * 
	 * @param str
	 * @param maxLength
	 * @return
	 */
	public static boolean isExeedMaxLength(String str, int maxLength) {
		if (str == null)
			return false;
		return str.length() > maxLength;
	}
	
	/**
	 * 合并多个数组
	 * 
	 * @param <T>
	 * @param first
	 * @param arrs
	 * @return
	 */
	public static <T> T[] concat(T[] first, T[]... arrs) {
		if (first == null) {
			logger.info("error: the first mast not be null, or will return null!");
			return null;
		}
		
		int totalLength = first.length;
		for (T[] array : arrs) {
			if (isNullOrEmpty(array)) {
				continue;
			}
			totalLength += array.length;
		}
		
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : arrs) {
			if (isNullOrEmpty(array)) {
				continue;
			}
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		
		return result;
	}

    /**
     * 按某字符分割字符串
     * @param str   待分割的字符串
     * @param regex 分割符
     * @return
     */
    public static String[] split(String str, String regex) {
        if(isNullOrEmpty(str)){
           return null;
        }
        if(isNullOrEmpty(regex)){
            String[] strs = new String[1];
            strs[0] = str;
            return strs;
        }else{
            return str.split(regex);
        }
    }
    
    /**
     * LIST 转为字符转 以 “，”隔开
     * @param list
     * @return
     */
    public static String tranformListToStr(List<String> list) {
    	return tranformListToStr(list, ",");
    }
    
    public static List<String> tranformStrToList(String str) {
    	return tranformStrToList(str,",");
    }
    
    public static List<String> tranformStrToList(String str,String regex) {
    	List<String>  list = new ArrayList<String>();
    	if(isNullOrEmpty(str)) {
    		return list;
    	}
    	if(str.indexOf(regex)==-1) {
    		list.add(str);
    	}else {
    		String[] arr = str.split(regex);
    		for(String ele:arr) {
    			list.add(ele);
    		}
    	}
    	return list;
    }
    
    /**
     * 将List类型转为数组类型
     * @param list
     * @return
     */
    public static String[] transformListToArr(List<String> list) {
    	if(isNullOrEmpty(list)) {
    		return null;
    	}
    	String[] strArr = new String[list.size()];
    	for(int i=0;i<list.size();i++) {
    		strArr[i] = list.get(i);
    	}
    	return strArr;
    }
    
    public static String tranformListToStr(List<String> list,String regex){
    	StringBuffer returnStr = new StringBuffer("");
    	if(null != list && list.size()>0) {
    	    for(int i=0;i<list.size();i++) {
    	    	if(i==list.size()-1) {
    	    		returnStr.append(list.get(i));
    	    	}else {
    	    		returnStr.append(list.get(i)+regex);
    	    	}
    	    }
    	}
    	return returnStr.toString();
    }
    
    /**
     * 获取一天的最小时间
     * @param dateStr
     * @return
     */
    public static String getMinimumTimeOfDay(String dateStr){
    	if(isNullOrEmpty(dateStr)) {
    		return "";
    	}
    	if(dateStr.length()>10) {
    		return dateStr;
    	}
    	return dateStr+" 00:00:00";
    }
    
    /**
     * 获取一天最大的时间
     * @param dateStr
     * @return
     */
    public static String getMaxTimeOfDay(String dateStr) {
    	if(isNullOrEmpty(dateStr)) {
    		return "";
    	}
    	if(dateStr.length()>10) {
    		return dateStr;
    	}
    	return dateStr+" 23:59:59";
    }
    
    
    /**
	 * 随机生成指定长度的字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomStr(int length) {
		char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
				.toCharArray();
		int rad = 0;
		String radStr = "";
		for (int i = 0; i < length; i++) {
			rad = (int) (Math.random() * 62);
			radStr += chars[rad];
		}
		return radStr;
	}
    
    /** 
     * 取得给定汉字串的首字母串,即声母串 
     * @param str 给定汉字串 
     * @return 声母串 
     */  
    /*public static String getAllFirstLetter(String str) {
        if (str == null || str.trim().length() == 0) {  
            return "";  
        }  
        String _str = "";
        for (int i = 0; i < str.length(); i++) {
            _str = _str +getFirstLetter(str.substring(i, i + 1));
        }  
        return _str;  
    }  */
	
	/**
	 * 将汉字转化为拼音
	 * @param str
	 * @return
	 */
	public static String convert2Pinyin(String str){
		if(isNullOrEmpty(str)){
			return "";
		}
		PinYinHelper parser = PinYinHelper.getInstance();
	    return parser.getPinYin(str);
	}
  
    /** 
     * 取得给定汉字的首字母,即声母 
     * @param chinese 给定的汉字 
     * @return 给定汉字的声母 
     */  
    /*public static String getFirstLetter(String chinese) {  
        if (chinese == null || chinese.trim().length() == 0) {  
            return "";  
        }  
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");  
        if (chinese.length() > 1){ // 判断是不是汉字  
            int li_SectorCode = (int) chinese.charAt(0); // 汉字区码  
            int li_PositionCode = (int) chinese.charAt(1); // 汉字位码  
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;  
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码  
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {  
                for (int i = 0; i < 23; i++) {
                    if (li_SecPosCode >= li_SecPosValue[i] && li_SecPosCode < li_SecPosValue[i + 1]) {  
                        chinese = lc_FirstLetter[i];  
                        break;
                    }  
                }  
            } else {// 非汉字字符,如图形符号或ASCII码  
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");  
                chinese = chinese.substring(0, 1);  
            }  
        }  
        return chinese;  
    } */ 
  
    /** 
     * 字符串编码转换 
     * @param str 要转换编码的字符串 
     * @param charsetName 原来的编码 
     * @param toCharsetName 转换后的编码 
     * @return 经过编码转换后的字符串 
     */  
    private static String conversionStr(String str, String charsetName,String toCharsetName) {  
        try {  
            str = new String(str.getBytes(charsetName), toCharsetName);  
        } catch (UnsupportedEncodingException ex) {  
            System.out.println("字符串编码转换异常：" + ex.getMessage());  
        }  
        return str;  
    }
    /**
     * 返回非null字符串
     * @param srcStr
     * @return
     */
    public static String getNotNullStr(String srcStr) {
    	if (srcStr == null) {
    		return "";
    	}
    	return srcStr;
    }
    
    /**
     * 返回非null字符串
     * @param obj
     * @return
     * @since 6.0
     */
    public static String getNotNullStr(Object obj) {
    	return isNullOrEmpty(obj) ? "" : String.valueOf(obj);
    }
    
    /**
     * 首字母大写
     * @param str
     * @return
     * @since 6.0
     */
    public static String upperFristLetter(String str) {
    	if (isNullOrEmpty(str)) {
    		return "";
    	}
    	return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
    }

	public static String getStringInMap(String string,
			Map<String, Object> queryParams) {
		Object obj = queryParams.get(string);
		if(null == obj){
			return null;
		}
		
		return obj + "";
	}

	public static boolean isEmptyInMap(Map<String, Object> map,
			String keyServiceErrorCode) {
		if(null == map || map.get(keyServiceErrorCode) == null){
			return true;
		}
		return false;
	}
    
}
