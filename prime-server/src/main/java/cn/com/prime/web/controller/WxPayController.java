package cn.com.prime.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.prime.common.base.BaseController;
import cn.com.prime.common.support.JSONView;
import cn.com.prime.common.support.JsonHelper;
import cn.com.prime.common.support.PaymentToolsFacade;
import cn.com.prime.common.support.SystemConstant;
import cn.com.prime.common.support.XMLParser;
import cn.com.prime.common.util.JaxbUtil;
import cn.com.prime.common.util.RedisFacade;
import cn.com.prime.common.util.StringUtil;
import cn.com.prime.common.util.SystemMessage;
import cn.com.prime.model.PublicPaymentOrder;
import cn.com.prime.model.QueryOrder;
import cn.com.prime.service.WxPayService;
/**
 * 微信支付控制器
 * @author yangchao.wang
 *	2016年12月6日
 */
@Controller
@RequestMapping("/wx")
public class WxPayController extends BaseController{

	@Autowired
	private WxPayService  payService;
	
	/**
	 * redis存储商户配置信息
	 */
	public static final String REDIS_PREFIX = "merchant_";
	
	/**
	 * 微信支付统一下单接口
	 * @param request
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/pay/unifiedorder", method = RequestMethod.POST, produces = "application/json")
	public JSONView unifiedorder(HttpServletRequest request , @RequestBody Map<String, Object> params){
		logger.info("微信公众号统一下单 请求 ->>" + JsonHelper.parseToJson(params));
		//获取企业配置信息
		Map<String, Object> configs = (Map<String, Object>)params.get("configs");
		JSONView jsonView = new JSONView();
		try {
			/**
			 * 组装订单信息
			 */
			PublicPaymentOrder order = new PublicPaymentOrder();
			order.setAppid(StringUtil.getStringInMap("thirdAppId", configs));//配置信息中获取 公众号appId
			order.setAttach(StringUtil.getStringInMap("tradeAttach", params));
			order.setMchId(StringUtil.getStringInMap("thirdMerId", configs));//配置中获取  第三方支付渠道的商户Id
			//设置随机字符串，本地工具生成
			order.setNonceStr(PaymentToolsFacade.getNonceStr());
			//设置微信支付结果回调地址
			order.setNotifyUrl(SystemMessage.getString("url_notify_url_wx"));
			order.setOpenid(StringUtil.getStringInMap("thirdUserId", params));
			order.setOutTradeNo(StringUtil.getStringInMap("orderNo", params));
			order.setSpbillCreateIp(StringUtil.getStringInMap("orderCreateIp", params));
			order.setTotalFee(StringUtil.getStringInMap("totalAmount", params));
			order.setTradeType(StringUtil.getStringInMap("tradeType", params));
			order.setBody(StringUtil.getStringInMap("goodsSubject", params));//设置商品主题
			order.setProductId(StringUtil.getStringInMap("productId", params));//设置商品Id  ，  native扫码支付必传
			Map<String,Object> signParams = XMLParser.getMapFromXML(JaxbUtil.toXml(order));
			logger.info("签名的参数--------" + JsonHelper.parseToJson(signParams));
			
			//redis设置或更新商户配置信息
			
			String redisKey = REDIS_PREFIX + order.getMchId();
			RedisFacade.set(redisKey, JsonHelper.parseToJson(configs));
			
			//设置签名
			order.setSign(PaymentToolsFacade.getSignByMap(signParams, (String)configs.get("thirdMerKey")));
			//请求微信，调用微信公众号统一下单接口，得到prepay_id等信息
			Map<String, Object> result = payService.unifiedorder(order,configs);
			
			// 检查统一下单结果
			if(isSuccServiceCall(result)){
				
				//JS-API参数需求组装
				if(order.getTradeType().equals("JSAPI")){
					Map<String, Object> orderRespInfo = new HashMap<String, Object>();
					orderRespInfo.put("appId", result.get("appid"));
					orderRespInfo.put("timeStamp", System.currentTimeMillis()/1000+"");
					orderRespInfo.put("nonceStr", result.get("nonceStr"));
					orderRespInfo.put("package", "prepay_id="+result.get("prepayId"));
					orderRespInfo.put("signType", "MD5");
					orderRespInfo.put("paySign", PaymentToolsFacade.getSignByMap(orderRespInfo, (String)configs.get("thirdMerKey")));
					result.put("orderRespInfo", orderRespInfo);
					jsonView.putAll(result);

				//NATIVE返回
				}else if(order.getTradeType().equals("NATIVE")){
					jsonView.putAll(result);
				}
				jsonView.setSuccess();
			}else{
				jsonView.setReturnCode((String)result.get(SystemConstant.KEY_SERVICE_ERROR_CODE));
				jsonView.setReturnMsg((String)result.get(SystemConstant.KEY_SERVICE_ERROR_MSG));
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonView.setReturnMsg("微信支付统一下单异常!");
			jsonView.setFail();
		}
		logger.info("微信公众号统一下单接口 返回 ->>" + jsonView);
		return jsonView;
	}
	
	
	
	/**
	 * 微信支付订单查询接口
	 * @param request
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/pay/queryOrder", method = RequestMethod.POST, produces = "application/json")
	public JSONView queryOrder(HttpServletRequest request , @RequestBody Map<String, Object> params){
		logger.info("微信支付订单查询 请求 ->>" + JsonHelper.parseToJson(params));
		JSONView jsonView = new JSONView();
		
		Map<String,Object> configs = (Map<String, Object>)params.get("configs");
		Map<String,Object> queryParams = (Map<String, Object>)params.get("order");
		try {
			//订单查询model 组装
			QueryOrder order = new QueryOrder();
			order.setAppid(StringUtil.getStringInMap("thirdAppId", configs));
			order.setMchId(StringUtil.getStringInMap("thirdMerId", configs));
			order.setNonceStr(PaymentToolsFacade.getNonceStr());
			order.setOutTradeNo(StringUtil.getStringInMap("merOrderNo", queryParams));
			order.setTransactionId(StringUtil.getStringInMap("thirdTradeNo", queryParams));
			Map<String,Object> signParams = XMLParser.getMapFromXML(JaxbUtil.toXml(order));
			logger.info("签名的参数--------" + JsonHelper.parseToJson(signParams));
			//设置签名
			order.setSign(PaymentToolsFacade.getSignByMap(signParams, (String)configs.get("thirdMerKey")));
			
			Map<String, Object> result = payService.queryOrder(order,configs);
			
			// 检查统一下单结果
			if(isSuccServiceCall(result)){
				jsonView.putAll(result);
				jsonView.setSuccess();
			}else{
				jsonView.setReturnCode((String)result.get(SystemConstant.KEY_SERVICE_ERROR_CODE));
				jsonView.setReturnMsg((String)result.get(SystemConstant.KEY_SERVICE_ERROR_MSG));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return getExeFailView("微信支付订单查询异常!");
		}
		logger.info("微信支付订单查询 返回 ->>" + jsonView);
		return jsonView;
	}
	
	
}
