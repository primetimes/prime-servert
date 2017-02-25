package cn.com.prime.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import cn.com.prime.common.support.ErrorConstants;
import cn.com.prime.common.util.JaxbUtil;
import cn.com.prime.common.util.StringUtil;
import cn.com.prime.common.util.SystemMessage;
import cn.com.prime.model.PaymentOrderDetail;
import cn.com.prime.model.PublicPaymentOrder;
import cn.com.prime.model.PublicPaymentOrderResult;
import cn.com.prime.model.QueryOrder;
import cn.com.prime.service.base.WxPayBaseService;

/**
 * 微信支付服务
 * @author yangchao.wang
 *	2016年12月6日
 */
@Service
public class WxPayService extends WxPayBaseService{

	/**
	 * 预下单
	 * @param order
	 * @return
	 */
	public Map<String, Object> unifiedorder(PublicPaymentOrder order,Map<String, Object> configs) throws Exception{
		String reqXML = JaxbUtil.toXml(order);
		String apiUrl = SystemMessage.getString("wx_payment_public_unifiedorder");
		logger.info("[payGateway] 统一下单请求url ->" + apiUrl);
		logger.info("[payGateway] 统一下单请求xml ->" + reqXML);
		String xml = template.postForObject(apiUrl, reqXML, String.class,this.newHashMap());
		logger.info("[payGateway] 统一下单结果xml ->" + xml);
		PublicPaymentOrderResult payResult = JaxbUtil.fromXml(xml, PublicPaymentOrderResult.class);
		
		//结果处理
		Map<String , Object> result = this.newHashMap();
		//网关调用服务异常， 则返回异常码
		if((result = this.parseBizResult(payResult)).size() != 0 ){
			return result;
		}
		
		//检查微信返回的签名
		if(!this.checkedPaymentResponseSign(xml,StringUtil.getStringInMap("thirdMerKey", configs))){
			logger.fatal("返回数据签名比对异常");
			return this.getErrInfo(ErrorConstants.WX_SIGN_CHECK_ERROR_CODE, ErrorConstants.WX_SIGN_CHECK_ERROR_MSG);
		}
		
		return this.transBean2Map(payResult);
	
	}

	/**
	 * 查询订单详情
	 * @param order
	 * @return
	 */
	public Map<String, Object> queryOrder(QueryOrder order, Map<String, Object> configs) {
		String reqXML = JaxbUtil.toXml(order);
		String apiUrl = SystemMessage.getString("wx_payment_public_queryorder");
		logger.info("[payGateway] 订单查询请求url ->" + apiUrl);
		logger.info("[payGateway] 订单查询请求xml ->" + reqXML);
		String xml = template.postForObject(apiUrl, reqXML, String.class,this.newHashMap());
		logger.info("[payGateway] 订单查询结果xml ->" + xml);
		PaymentOrderDetail payResult = JaxbUtil.fromXml(xml, PaymentOrderDetail.class);
		
		//结果处理
		Map<String , Object> result = this.newHashMap();
		//网关调用服务异常， 则返回异常码
		if((result = this.parseBizResult(payResult)).size() != 0 ){
			return result;
		}
		//检查微信返回的签名
		if(!this.checkedPaymentResponseSign(xml,StringUtil.getStringInMap("thirdMerKey", configs))){
			logger.fatal("返回数据签名比对异常");
			return this.getErrInfo(ErrorConstants.WX_SIGN_CHECK_ERROR_CODE, ErrorConstants.WX_SIGN_CHECK_ERROR_MSG);
		}
		
		return this.transBean2Map(payResult);
	
	}
	

}
