package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author yangchao.wang
 *	2016年9月19日
 */
public class BaseOrderResponse extends BaseOrder{

	private String returnCode;
	private String returnMsg;
	private String resultCode;
	private String resultMsg;
	private String errCode;
	private String errCodeDes;
	
	@XmlElement(name = "return_code")
	public String getReturnCode() {
		return returnCode;
	}
	@XmlElement(name = "return_msg")
	public String getReturnMsg() {
		return returnMsg;
	}
	@XmlElement(name = "result_code")
	public String getResultCode() {
		return resultCode;
	}
	@XmlElement(name = "result_msg")
	public String getResultMsg() {
		return resultMsg;
	}
	@XmlElement(name = "err_code")
	public String getErrCode() {
		return errCode;
	}
	
	@XmlElement(name = "err_code_des")
	public String getErrCodeDes() {
		return errCodeDes;
	}
	
	
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public void setErrCodeDes(String errCodeDes) {
		this.errCodeDes = errCodeDes;
	}
	
	
}
