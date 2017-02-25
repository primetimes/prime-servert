package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 支付通知消息处理后返回model
 * @author yangchao.wang
 *	2016年9月19日
 */
@XmlRootElement(name="xml")
public class PaymentNotifyResponse {

	private String returnCode;
	private String returnMsg;
	
	@XmlElement(name = "return_code")
	public String getReturnCode() {
		return returnCode;
	}
	
	@XmlElement(name = "return_msg")
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	
	
}
