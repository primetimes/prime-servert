package cn.com.prime.common.support;
import java.nio.charset.Charset;

/**
 * 全局常量
 * @author Rain
 *
 */
public class Constants {
	
	/**
	 * 通用的值:NO
	 */
    public static final String NO = "0";
    /**
     * 通用的值:YES
     */
    public static final String YES = "1";
    
    /**
     * UTF-8字符集
     */
    public static final Charset UTF8 = Charset.forName("UTF-8");
    
    public static final String HTTP_METHOD_POST = "POST";

	public static final String HTTP_METHOD_GET = "GET";
    
    /**
     * 返回结果
     * 执行成功200
     */
    public final static String RETURN_SUCC= "200";
    
    /**
     * 返回结果
     * 执行成功201
     */
    public final static String RETURN_NO_SP= "201";
    
    /**
     * 返回结果
     * 数据异常统一返回502
     */
	public final static String RETURN_DATA_ERROR= "502";
	
	/**
     * 返回结果
     * 数据重复503
     */
	public final static String RETURN_DOUBLE_ERROR= "503";
	
	/**
     * 返回结果
     * 数据违反长度约束503
     */
	public final static String RETURN_MAX_ERROR= "508";
	
	/**
     * 返回结果
     * 数据为空500
     */
	public final static String RETURN_NULL_ERROR= "500";
	
	/**
     * 返回结果
     * 执行失败499
     */
	public final static String RETURN_EXE_FAIL= "499";
	
	/**
	 * 业务类型ID
	 * 短信
	 */
	public static final String PRODUCT_DX = "1";

	/**
	 * 业务类型：彩信
	 */
	public static final String PRODUCT_CX = "2";

	/**
	 * 业务类型ID
	 * E信
	 */
	public static final String PRODUCT_EX = "3";

	/**
	 * 业务类型微信
	 */
	public static final String PRODUCT_WX = "4";

	/**
	 * 业务类型彩E信
	 */
	public static final String PRODUCT_CEX = "5";
	
	
	/**
	 * 业务类型yxt
	 */
	public static final String PRODUCT_YXT = "6";
	
	public static final String SOURCE = "spApi";
	
	//动态数据，手机号码key
	public static final String DYN_MDN_KEY = "手机号码";
	
	/**
	 * 发送中
	 */
	public static final String STATUS_SENDING = "0";
	
	/**
	 * 待审核
	 */
	public static final String STATUS_AUDITING = "1";
	
	/**
	 * 发送完成
	 */
	public static final String STATUS_SUCC = "4";
	
	/**
	 * 发送失败
	 */
	public static final String STATUS_FAIL = "2";
	
	/**
	 * 接口服务名称
	 */
	public static final String SERVICE_NAME_CMC = "cmc";
	public static final String SERVICE_NAME_FLOW = "flow";
	
	/**
	 * API接口类型
	 */
	public static final String API_TYPE_CMC = "1";
	public static final String API_TYPE_FLOW = "2";
	
	/**
	 * 企业签名位置
	 */
	public static final String SP_SIGN_POS_BEFORE = "0"; // 前置
	public static final String SP_SIGN_POS_AFTER = "1"; // 后置
	
}
