package cn.com.prime.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import cn.com.prime.common.support.CDataAdapter;

/**
 * 微信公众号统一下单 model
 * @author yangchao.wang
 *	2016年9月19日
 */
@XmlRootElement(name = "xml")
public class PublicPaymentOrder extends BaseOrder{

	/**
	 *设备号
	 */
	private String deviceInfo;
	/**
	 * 商品描述
	 */
	private String body;
	/**
	 * 商品详情
	 */
	private String detail;
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
	 * 终端IP
	 */
	private String spbillCreateIp;
	/**
	 * 交易起始时间
	 */
	private String timeStart;
	/**
	 * 交易结束时间
	 */
	private String timeExpire;
	/**
	 * 商品标记
	 */
	private String goodsTag;
	/**
	 * 通知地址
	 */
	private String notifyUrl;
	/**
	 * 交易类型
	 */
	private String tradeType;
	/**
	 * 商品id
	 */
	private String productId;
	/**
	 * 指定支付方式
	 */
	private String limitPay;
	/**
	 * 用户标识
	 */
	private String openid;
	
	
	@XmlElement(name="device_info")
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
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
	@XmlElement(name="spbill_create_ip")
	public String getSpbillCreateIp() {
		return spbillCreateIp;
	}
	public void setSpbillCreateIp(String spbillCreateIp) {
		this.spbillCreateIp = spbillCreateIp;
	}
	@XmlElement(name="time_start")
	public String getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}
	@XmlElement(name="time_expire")
	public String getTimeExpire() {
		return timeExpire;
	}
	public void setTimeExpire(String timeExpire) {
		this.timeExpire = timeExpire;
	}
	
	@XmlElement(name="goods_tag")
	public String getGoodsTag() {
		return goodsTag;
	}
	public void setGoodsTag(String goodsTag) {
		this.goodsTag = goodsTag;
	}
	@XmlElement(name="notify_url")
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	@XmlElement(name="trade_type")
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	@XmlElement(name="product_id")
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	@XmlElement(name="limit_pay")
	public String getLimitPay() {
		return limitPay;
	}
	public void setLimitPay(String limitPay) {
		this.limitPay = limitPay;
	}
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
}
