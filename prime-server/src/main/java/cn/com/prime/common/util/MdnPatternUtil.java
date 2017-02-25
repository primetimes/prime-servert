package cn.com.prime.common.util;

import java.util.regex.Pattern;

/**
 * @desc 手机号码校验工具类
 * @author qiaolong.lu@flaginfo.com.cn
 * @date 2016年6月23日
 * @since jdk1.7
 * @version 6.0
 */
public class MdnPatternUtil {
	
	private static final String LT_REGEXP = SystemMessage.getString("lt_regexp"); //联通
	private static final String DX_REGEXP = SystemMessage.getString("dx_regexp"); //电信
	private static final String YD_REGEXP = SystemMessage.getString("yd_regexp"); //移动
	private static final String XL_REGEXP = SystemMessage.getString("xl_regexp"); //小灵通
	private static final String GW_REGEXP = SystemMessage.getString("gw_regexp"); //国外

	private static final Pattern lt = Pattern.compile(LT_REGEXP);
	private static final Pattern dx = Pattern.compile(DX_REGEXP);
	private static final Pattern yd = Pattern.compile(YD_REGEXP);
	private static final Pattern xl = Pattern.compile(XL_REGEXP);
	private static final Pattern gw = Pattern.compile(GW_REGEXP);
	
	public static final int MDN_LT = 1;   //联通手机号
	public static final int MDN_YD = 2;   //移动手机号
	public static final int MDN_DX = 3;   //电信手机号
	public static final int MDN_GW = 4;   //国外手机号
	public static final int MDN_INVALID = -1;   //非手机号
	

	/**
	 * 判断是否是手机号, 国内号码不包含前缀
	 * @param mdn
	 * @return
	 */
	public static MdnType validateMdn(String mdn) {
		if (lt.matcher(mdn).matches()) {
			return MdnType.LT;
		} else if (yd.matcher(mdn).matches()) {
			return MdnType.YD;
		} else if (dx.matcher(mdn).matches()) {
			return MdnType.DX;
		} else if (gw.matcher(mdn).matches()) {
			return MdnType.GW;
		} else {
			return MdnType.INVALID;
		}
	}
	
	/**
	 * 删除手机号码前缀
	 * @param mdns 手机号码
	 * @return
	 */
	public static String removeMdnPrefix(String mdns) {
		StringBuffer sb = new StringBuffer();
		String[] mdnArr = mdns.split(";");
		for (String mdn : mdnArr) {
			// 中国区去掉前缀和区号，国际区去掉+和00
			if (mdn.startsWith("+86")) {
				mdn = mdn.substring(3);
			} else if (mdn.startsWith("86")) {
				mdn = mdn.substring(2);
			} else if (mdn.startsWith("0086")) {
				mdn = mdn.substring(4);
			} else if (mdn.startsWith("+")) {
				mdn = mdn.substring(1);
			} else if (mdn.startsWith("00")) {
				mdn = mdn.substring(2);
			}
			sb.append(mdn);
			sb.append(";");
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	/**
	 * 校验并返回不合法手机号码
	 * @param mdns
	 * @return 非法手机号码
	 */
	public static String getInvalidMdn(String mdns) {
		StringBuffer sb = new StringBuffer();
		String[] mdnArr = mdns.split(";");
		for (String mdn : mdnArr) {
			if (validateMdn(mdn) == MdnType.INVALID) {
				sb.append(mdn);
				sb.append(",");
			}
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String mdns = "008613512134896;31231231231;+12312312;0012312312";
		String invalid = getInvalidMdn(mdns);
		System.out.println(invalid);
		String mdn = removeMdnPrefix(mdns);
		System.out.println(mdn);
	}
	
}
