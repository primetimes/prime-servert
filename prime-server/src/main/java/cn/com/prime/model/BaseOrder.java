package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;

public class BaseOrder {

	private String appid;
	private String mchId;
	private String nonceStr;
	private String sign;
	public String getAppid() {
		return appid;
	}
	@XmlElement(name = "mch_id")
	public String getMchId() {
		return mchId;
	}
	@XmlElement(name = "nonce_str")
	public String getNonceStr() {
		return nonceStr;
	}
	public String getSign() {
		return sign;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public void setMchId(String mchId) {
		this.mchId = mchId;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}
