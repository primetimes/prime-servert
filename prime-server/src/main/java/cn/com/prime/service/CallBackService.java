package cn.com.prime.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import cn.com.prime.common.support.JSONView;
import cn.com.prime.common.support.JsonHelper;
import cn.com.prime.common.util.JaxbUtil;
import cn.com.prime.common.util.RedisFacade;
import cn.com.prime.common.util.SystemMessage;
import cn.com.prime.model.PaymentNotifyResponse;
import cn.com.prime.model.PaymentOrderDetail;
import cn.com.prime.model.ReturnCodeType;
import cn.com.prime.service.base.WxPayBaseService;
import cn.com.prime.web.controller.WxPayController;

/**
 * 回调处理服务
 * @author yangchao.wang
 *	2016年12月7日
 */
@Service
public class CallBackService extends WxPayBaseService{

	@SuppressWarnings("unchecked")
	public String wxNotifyCallBack(String xml) {
		PaymentOrderDetail order = JaxbUtil.fromXml(xml, PaymentOrderDetail.class);
		
		//redis去重检查
		String appId = order.getAppid();
		String orderNo = order.getOutTradeNo();
		String openId = order.getOpenid();
		
		//公众号Id + 商户订单号 + 粉丝  三个维度确定一条记录
		String key = appId + "_"+orderNo +  "_"+openId;
		if(RedisFacade.exists(key)){
			logger.error("警告：重复的回调请求！");
			PaymentNotifyResponse response = new PaymentNotifyResponse();
			response.setReturnCode("SUCCESS");
			xml = JaxbUtil.toXml(response);
			return xml;
		}else{
			RedisFacade.set(key, 1);
		}
		
		
		//从redis中获取企业支付配置信息（该信息是在统一下单时存储、更新）
		String configsKey = WxPayController.REDIS_PREFIX + order.getMchId();
		Map<String,Object> configs = (Map<String, Object>)RedisFacade.get(configsKey, Map.class);
		Map<String , Object> reqMap = this.transBean2Map(order);
		
		if(!this.checkedPaymentResponseSign(xml,(String)configs.get("thirdMerKey")) && order.getReturnCode().equals(ReturnCodeType.SUCCESS.toString())){
			logger.fatal("返回数据签名比对异常");
			throw new RuntimeException("返回数据签名比对异常！");
		}
		
		//微信回调标识
		reqMap.put("wx", "1");
		return doCallBack(reqMap);
	}

	
	/**
	 * 回调
	 * @param reqMap
	 * @return
	 */
	private String doCallBack(Map<String, Object> reqMap) {
		String url = SystemMessage.getString("platform_payment_nofity");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json;charset=UTF-8");
		headers.add("Accept", "application/json");
		headers.add("Accept", "text/html");
		JSONView jsonView = new JSONView();
		jsonView.put("data", reqMap);
		HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(jsonView, headers);
		logger.info("支付通知回调地址->" + url);
		logger.info("支付通知请求参数->" + JsonHelper.parseToJson(reqMap));
		Map<String, Object> resultMap = template.postForObject(url, request, Map.class);
		logger.info("支付通知回调结果->" + JsonHelper.parseToJson(resultMap));
		
		String xml;
		if(isSucc(resultMap)){
			PaymentNotifyResponse response = new PaymentNotifyResponse();
			response.setReturnCode("SUCCESS");
			xml = JaxbUtil.toXml(response);
		}else{
			PaymentNotifyResponse response = new PaymentNotifyResponse();
			response.setReturnCode("FAIL");
			xml = JaxbUtil.toXml(response);
		}
		logger.info("支付通知返回结果->" + xml);
		return xml;
	}
	
	private boolean isSucc(Map<String, Object> resultMap) {
		if(resultMap.get("returnCode").equals("200")){
			return true;
		}
		return false;
	}
	
}
