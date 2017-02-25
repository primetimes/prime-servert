package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 支付订单查询model
 * @author yangchao.wang
 *	2016年9月19日
 */
@XmlRootElement(name = "xml")
public class CloseOrder extends BaseOrder{
	/**
	 * 商户订单号 
	 */
	private String outTradeNo;
	
	@XmlElement(name = "out_trade_no")
	public String getOutTradeNo() {
		return outTradeNo;
	}
	

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	
	
}
