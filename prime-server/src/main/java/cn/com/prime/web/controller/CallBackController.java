package cn.com.prime.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.prime.common.base.BaseController;
import cn.com.prime.service.CallBackService;
/**
 * 回调控制器
 * @author yangchao.wang
 *	2016年12月6日
 */
@Controller
@RequestMapping("/callBack")
public class CallBackController extends BaseController{

	@Autowired
	private CallBackService callbackService;

	/**
	 * 微信支付回调通知
	 * @param request
	 * @param paramMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/wx/notify", method = RequestMethod.POST,  produces = { "application/xml"})
	public String paymentNotify(HttpServletRequest request, @RequestBody String xml) {
		logger.info("微信支付通知消息 ->>" + xml);
		try {
			String resultXml = callbackService.wxNotifyCallBack(xml); 
			return resultXml;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("微信支付通知消息处理异常 ->>", e);
			return "";
		}
	}
	
}
