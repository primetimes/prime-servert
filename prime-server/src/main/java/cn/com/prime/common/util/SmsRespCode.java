package cn.com.prime.common.util;

/**
 * 内部短信功能返回码
 * @author Perlin
 * @date 2016年5月20日
 * @since 6.0
 * @version 6.0
 */
public enum SmsRespCode {
	
	resp_0("0", "成功"), 
	resp_1("1", "提交参数不能为空"), 
	resp_2("2", "账号无效或权限不足"), 
	resp_3("3", "账号密码错误"), 
	resp_4("4", "预约发送时间格式不正确，应为yyyyMMddHHmmss"),
	resp_5("5", "IP不合法"), 
	resp_6("6", "号码中含有无效号码或不在规定的号段或为免打扰号码（含系统黑名单号码）"), 
	resp_7("7", "非法关键字"), 
	resp_8("8", "内容长度超过上限，最大402字或字符"), 
	resp_9("9", "接受号码过多，最大1000"), 
	resp_11("11", "提交速度太快"), 
	resp_12("12", "您尚未订购[普通短信业务]，暂不能发送该类信息"), 
	resp_13("13", "您的[普通短信业务]剩余数量发送不足，暂不能发送该类信息"), 
	resp_14("14", "流水号格式不正确"), 
	resp_15("15", "流水号重复"), 
	resp_16("16", "超出发送上限（操作员帐户当日发送上限）"), 
	resp_17("17", "余额不足"),
	resp_18("18", "扣费不成功"),
	resp_20("20", "系统错误"), 
	resp_21("21", "密码错误次数达到5次"),
	resp_24("24", "帐户状态不正常"), 
	resp_25("25", "账户权限不足"), 
	resp_26("26", "需要人工审核"),
	resp_28("28", "发送内容与模板不符"),
	resp_29("29", "扩展号太长或不是数字"),
	resp_32("32", "同一号码相同内容发送次数太多");
	
	/**
	 * 返回码
	 */
	private String code;
	/**
	 * 描述
	 */
	private String desc;
	
	private SmsRespCode(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDesc() {
		return desc;
	}
}
