package cn.com.prime.common.util;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置 1.先加载本地配置文件 2.如果配置需要从远程读取,那么获取远程文件配置覆盖掉本地配置
 * 
 * @author Rain
 *
 */
public class SystemMessage {

	private final static Logger logger = LoggerFactory
			.getLogger(SystemMessage.class);
	private static Properties properties = new Properties();

	// 本地配置文件
	private static final String BUNDLE_NAME = "system";

	static {
		// 开启本地读取模式
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
		properties = new Properties();
		Enumeration<String> e = bundle.getKeys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			properties.put(key, bundle.getString(key));
		}
		/*if (bundle == null || !"1".equals(bundle.getString("local_conf"))) {
			DiamondManager manager = new DefaultDiamondManager(
					bundle.getString("prop_group"),
					bundle.getString("prop_data_id"), new ManagerListener() {
						public void receiveConfigInfo(String configInfo) {
							properties
									.putAll(getPropertiesForString(configInfo));
						}

						public Executor getExecutor() {
							return null;
						}
					});
			properties.putAll(manager
					.getAvailablePropertiesConfigureInfomation(5000));
		} else {
			logger.info("----------------------------------------------");
			logger.info("----开启本地读取模式，读取本地配置system--------");
			logger.info("----------------------------------------------");
		}*/

	}

	private static Properties getPropertiesForString(String configInfo) {
		Properties p = new Properties();
		try {
			p.load(new StringReader(configInfo));
			if (logger.isDebugEnabled()) {
				Set<Entry<Object, Object>> set = properties.entrySet();
				Map allMap = new HashMap();
				for (Entry e : set) {
					logger.debug(e.getKey() + "==>" + e.getValue());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	public static Map getAllConfig() {

		Set<Entry<Object, Object>> set = properties.entrySet();
		Map allMap = new HashMap();
		for (Entry e : set) {
			allMap.put(e.getKey(), e.getValue());
		}
		return allMap;
	}

	public static String getString(String key) {
		return properties.getProperty(key);
	}

	/**
	 * 获取配置，并将配置值转换为整数
	 * 
	 * @param key
	 * @return
	 */
	public static Integer getInteger(String key) {
		String value = getString(key);
		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取配置，并将配置值转换为Long类型
	 * 
	 * @param key
	 * @return
	 */
	public static Long getLong(String key) {
		String value = getString(key);
		try {
			return Long.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取配置，并将配置值转换为Double类型
	 * 
	 * @param key
	 * @return
	 */
	public static Double getDouble(String key) {
		String value = getString(key);
		try {
			return Double.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取配置，并将配置值转换为Float类型
	 * 
	 * @param key
	 * @return
	 */
	public static Float getFloat(String key) {
		String value = getString(key);
		try {
			return Float.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取配置，并将配置值转换为bool类型
	 * 
	 * @param key
	 * @return
	 */
	public static Boolean getBoolean(String key) {
		String value = getString(key);
		try {
			return Boolean.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取配置，并将配置值转换为BigDecimal类型
	 * 
	 * @param key
	 * @return
	 */
	public static BigDecimal getBigDecimal(String key) {
		String value = getString(key);
		try {
			return new BigDecimal(value);
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(getAllConfig());
	}

}
