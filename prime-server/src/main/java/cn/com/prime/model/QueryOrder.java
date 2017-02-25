package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 支付订单查询model
 * @author yangchao.wang
 *	2016年9月19日
 */
@XmlRootElement(name = "xml")
public class QueryOrder extends BaseOrder{
	
	/**
	 * 微信订单号
	 */
	private String transactionId;
	/**
	 * 商户订单号  （注意： 商户订单号和微信订单号二者选择其一）
	 */
	private String outTradeNo;
	
	@XmlElement(name = "transaction_id")
	public String getTransactionId() {
		return transactionId;
	}
	@XmlElement(name = "out_trade_no")
	public String getOutTradeNo() {
		return outTradeNo;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	
	
}
