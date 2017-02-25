package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信公众号统一下单结果 model
 * @author yangchao.wang
 *	2016年9月19日
 */
@XmlRootElement(name = "xml")
public class PublicPaymentOrderResult extends BaseOrderResponse{
	private String deviceInfo;
	private String tradeType;
	private String prepayId;
	private String codeUrl;
	
	

	@XmlElement(name = "device_info")
	public String getDeviceInfo() {
		return deviceInfo;
	}

	@XmlElement(name = "trade_type")
	public String getTradeType() {
		return tradeType;
	}
	@XmlElement(name = "prepay_id")
	public String getPrepayId() {
		return prepayId;
	}
	@XmlElement(name = "code_url")
	public String getCodeUrl() {
		return codeUrl;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}
	
	
	/**
	 * 判断结果状态， 成功or失败
	 * @return
	 */
	public boolean isSucc(){
		if(null != this.getResultCode()){
			return this.getReturnCode().equals(ReturnCodeType.SUCCESS.toString());
		}
		return false;
	}
}
