package cn.com.prime.common.support;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 支付工具类-facade
 * @author yangchao.wang
 *	2016年9月20日
 */
public class PaymentToolsFacade {
	private static final Logger logger = Logger.getLogger(PaymentToolsFacade.class);

	/**
	 * 随机字符串的长度
	 */
	private static int NONCE_STR_LENGTH = 16;
	

	/**
	 * 生成随机字符串
	 * @return
	 */
	public static String getNonceStr(){
		return RandomStringGenerator.getRandomStringByLength(NONCE_STR_LENGTH);
	}
	
	/**
	 * 生成签名
	 * @param obj
	 * @param key
	 * @return
	 */
	public static String getSignByObj(Object obj, String key){
		try {
			return Signature.getSignByObject(obj, key);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			logger.error("生成签名失败！-->>", e);
			throw new RuntimeException("签名生成失败！");
		}
	}
	
	/**
	 * 生成签名
	 * @param obj
	 * @param key
	 * @return
	 */
	public static String getSignByMap(Map<String,Object> params, String key){
		try {
			return Signature.getSignByMap(params, key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("生成签名失败！-->>", e);
			throw new RuntimeException("签名生成失败！");
		}
	}
	
	
	/**
	 * 检查返回消息中的签名是否正确
	 * @param xml
	 * @param key
	 * @return
	 */
	public static boolean checkedSignByResponseXML(String xml, String key){
		try {
			return Signature.checkIsSignValidFromResponseString(xml, key);
		} catch (Exception e) {
			logger.error("返回消息的签名检查异常", e);
			e.printStackTrace();
			throw new RuntimeException("签名检查异常");
		}
	}
}
