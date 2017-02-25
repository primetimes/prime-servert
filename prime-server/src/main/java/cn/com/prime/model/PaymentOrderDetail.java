package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信支付订单详情
 * @author yangchao.wang
 *	2016年9月19日
 */
@XmlRootElement(name = "xml")
public class PaymentOrderDetail extends BaseOrderResponse{

	private String isSubscribe;
	private String tradeState;
	private String bankType;
	private String settlementTotalFee;
	private String cashFee;
	private String cashFeeType;
	private String couponFee;
	private String couponCount;
	private String transactionId;//微信订单号
	private String timeEnd;
	private String tradeStateDesc;//交易状态描述
	
	
	/**
	 *设备号
	 */
	private String deviceInfo;
	/**
	 * 附加数据
	 */
	private String attach;
	/**
	 * 商户订单号
	 */
	private String outTradeNo;
	/**
	 * 货币类型
	 */
	private String feeType;
	/**
	 * 总金额
	 */
	private String totalFee;
	/**
	 * 交易类型
	 */
	private String tradeType;

	/**
	 * 用户标识
	 */
	private String openid;
	
	
	@XmlElement(name="is_subscribe")
	public String getIsSubscribe() {
		return isSubscribe;
	}
	
	@XmlElement(name="trade_state")
	public String getTradeState() {
		return tradeState;
	}
	@XmlElement(name="bank_type")
	public String getBankType() {
		return bankType;
	}
	@XmlElement(name="settlement_total_fee")
	public String getSettlementTotalFee() {
		return settlementTotalFee;
	}
	
	@XmlElement(name="cash_fee")
	public String getCashFee() {
		return cashFee;
	}
	@XmlElement(name="cash_fee_type")
	public String getCashFeeType() {
		return cashFeeType;
	}
	
	@XmlElement(name="coupon_fee")
	public String getCouponFee() {
		return couponFee;
	}
	
	@XmlElement(name="coupon_count")
	public String getCouponCount() {
		return couponCount;
	}
	
	@XmlElement(name="transaction_id")
	public String getTransactionId() {
		return transactionId;
	}
	
	@XmlElement(name="time_end")
	public String getTimeEnd() {
		return timeEnd;
	}
	
	@XmlElement(name="trade_state_desc")
	public String getTradeStateDesc() {
		return tradeStateDesc;
	}
	
	public void setIsSubscribe(String isSubscribe) {
		this.isSubscribe = isSubscribe;
	}
	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public void setSettlementTotalFee(String settlementTotalFee) {
		this.settlementTotalFee = settlementTotalFee;
	}
	public void setCashFee(String cashFee) {
		this.cashFee = cashFee;
	}
	public void setCashFeeType(String cashFeeType) {
		this.cashFeeType = cashFeeType;
	}
	public void setCouponFee(String couponFee) {
		this.couponFee = couponFee;
	}
	public void setCouponCount(String couponCount) {
		this.couponCount = couponCount;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}
	public void setTradeStateDesc(String tradeStateDesc) {
		this.tradeStateDesc = tradeStateDesc;
	}
	@XmlElement(name="device_info")
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	@XmlElement(name="out_trade_no")
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	@XmlElement(name="fee_type")
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	
	@XmlElement(name="total_fee")
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	
	@XmlElement(name="trade_type")
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
}
