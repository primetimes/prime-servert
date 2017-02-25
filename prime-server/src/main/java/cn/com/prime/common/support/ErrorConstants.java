package cn.com.prime.common.support;

/**
 * 返回的错误信息
 * @author yangchao.wang
 *	2016年12月6日
 */
public class ErrorConstants {

	/**
	 * 签名检查异常返回码
	 */
	public static final String WX_SIGN_CHECK_ERROR_CODE = "900001";
	public static final String WX_SIGN_CHECK_ERROR_MSG = "微信返回签名错误-";
	
	/**
	 * 网关调用微信通信异常返回码
	 */
	public static final String WX_GATEWAY_INVOKE_SER_ERROR_CODE = "900002";
	public static final String WX_GATEWAY_INVOKE_SER_ERROR_MSG = "微信网关通信错误-";
	
	/**
	 * 网关调用微信业务异常返回码
	 */
	public static final String WX_GATEWAY_INVOKE_BIZ_ERROR_CODE = "900003";
	public static final String WX_GATEWAY_INVOKE_BIZ_ERROR_MSG = "微信网关业务异常-";
}
