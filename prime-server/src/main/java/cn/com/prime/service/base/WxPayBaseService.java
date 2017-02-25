package cn.com.prime.service.base;

import java.util.Map;

import cn.com.prime.common.base.JsonClientService;
import cn.com.prime.common.support.ErrorConstants;
import cn.com.prime.model.BaseOrderResponse;
import cn.com.prime.model.ReturnCodeType;

/**
 * 微信支付基础服务
 * @author yangchao.wang
 *	2016年12月6日
 */
public abstract class WxPayBaseService extends JsonClientService{

	/**
	 * 解析微信支付的业务结果
	 * @param result
	 * @return
	 */
	public Map<String, Object>  parseBizResult(BaseOrderResponse result){
		//调用微信 通信非成功返回
		if(!result.getReturnCode().equals(ReturnCodeType.SUCCESS.toString())){
			String errMsg = result.getReturnMsg();
			logger.error("微信网关调用异常返回-->> errMsg=" + errMsg);
			return this.getErrInfo(ErrorConstants.WX_GATEWAY_INVOKE_SER_ERROR_CODE, ErrorConstants.WX_GATEWAY_INVOKE_SER_ERROR_MSG + "["+ errMsg +"]");
			
		}
		//调用微信 业务处理非成功返回
		if(!result.getResultCode().equals(ReturnCodeType.SUCCESS.toString())){
			String errCode = result.getErrCode();
			String errCodeDes = result.getErrCodeDes();
			logger.error("微信网关调用异常返回-->> errorCode=" + errCode + ", errCodeDes=" + errCodeDes);
			return this.getErrInfo(ErrorConstants.WX_GATEWAY_INVOKE_BIZ_ERROR_CODE, ErrorConstants.WX_GATEWAY_INVOKE_SER_ERROR_MSG + "["+ errCodeDes +"]");
		}
		
		return this.newHashMap();
		
	}
	
}
