package cn.com.prime.web.controller.user;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.prime.common.base.BaseController;
import cn.com.prime.common.support.JSONView;
import cn.com.prime.common.support.JsonHelper;
import cn.com.prime.common.support.RandomStringGenerator;
import cn.com.prime.common.support.ServiceProxy;
import cn.com.prime.common.util.StringUtil;
import cn.com.prime.model.user.UserModel;
import cn.com.prime.service.user.UserService;
/**
 * 用户管理控制器
 * @author yangchao.wang
 *	2016年12月6日
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

	
	/**
	 * 微信网页授权url
	 */
	private static final String WebAuthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
			+ "appid={1}&redirect_uri={2}&response_type=code&scope={3}&state={4}#wechat_redirect";
	
	/**
	 * 获取网页授权的tocken
	 */
	private static final String WebAuthUrl_AccessTocken="https://api.weixin.qq.com/sns/oauth2/access_token?"
			+ "appid={1}&secret={2}&code={3}&grant_type=authorization_code";
	
	
	/**
	 * 网页授权获取用户信息
	 */
	private static final String WebAuthUrl_Userinfo = "https://api.weixin.qq.com/sns/userinfo?"
			+ "access_token={1}&openid={2}&lang=zh_CN";
	
	private static final String appid = "xxxxxxxxxxxxxx";
	private static final String secret = "xxxxxxxxxxxxxx";
	
	private UserService  userService = ServiceProxy.getInstance(UserService.class);
	
	private static Map<String, String> urlsMap = new HashMap<String, String>();
	
	/**
	 * 获取微信用户详细
	 * @param request
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@RequestMapping(value = "/wx/get-detail")
	public void getDetail(HttpServletRequest request, HttpServletResponse response){
		logger.debug("urlsMap size = " + urlsMap.size());
		Map<String, Object> params = this.bindParamToMap(request);
		
		//判断是否是请求消息
		if(StringUtil.isNotNullOrEmpty(params.get("isRequest"))){
			
			//获取跳转页面
			String redirectUrl = StringUtil.getStringInMap("redirectUrl", params);
			if(StringUtil.isNullOrEmpty(redirectUrl)){
				throw new RuntimeException("redirectUrl = null");
			}
			//随机生成状态参数
			String state = System.currentTimeMillis() + RandomStringGenerator.getRandomStringByLength(20);
			String scope = "1".equals( params.get("isFollowed"))? "snsapi_base":"snsapi_userinfo";
			String sendRedirectUrl = request.getContextPath() + "/wx/get-detail";
			String encUrl = URLEncoder.encode(sendRedirectUrl);
			sendRedirectUrl = String.format(WebAuthUrl, appid, encUrl,scope,state);
			logger.debug("组装的重定向URL-->>" + sendRedirectUrl);
			
			try {
				response.sendRedirect(sendRedirectUrl);
				logger.debug("组装的重定向URL Success!");
				urlsMap.put(state, redirectUrl);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("微信网页授权重定向失败！", e);
			}
			
			return;
		}
		
		
		//判断是否是微信回调的消息
		if(StringUtil.isNotNullOrEmpty(params.get("state"))){
			try{
				//用户授权code存在
				if(!StringUtil.isEmptyInMap(params, "code")){
					String code = params.get("code") + "";
					String toGetUrl = String.format(WebAuthUrl_AccessTocken,appid,secret, code);
					logger.debug("请求并获取access_token  url-->" + toGetUrl);
					Map<String, Object> result = userService.getForObject(toGetUrl,null,Map.class);
					//请求access_token成功返回
					if(StringUtil.isEmptyInMap(result, "errcode")){
						String openId = result.get("openid") + "";
						String access_token = result.get("access_token") + "";
						String userinfoUrl = String.format(WebAuthUrl_Userinfo, access_token, openId);
						logger.debug("请求并获取用户信息 url-->" + userinfoUrl);
						result = userService.getForObject(userinfoUrl, null, Map.class);
					}
					
					//请求userinfo成功返回
					if(StringUtil.isEmptyInMap(result, "errcode")){
						request.setAttribute("json-data", JsonHelper.parseToJson(result));
						request.getRequestDispatcher(urlsMap.remove(params.get("state"))).forward(request, response);
						logger.debug("页面跳转成功");
						
					}
					
					response.sendRedirect(urlsMap.remove(params.get("state")));
					logger.info("API调用错误， 授权失败！");
					urlsMap.remove(params.get("state"));
					
					//失败处理
				}else{
					response.sendRedirect(urlsMap.remove(params.get("state")));
					logger.info("用户未授权， 授权失败！");
					urlsMap.remove(params.get("state"));
				}

			}catch(Exception e){
				e.printStackTrace();
				try {
					response.sendRedirect(urlsMap.remove(params.get("state")));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logger.error("异常->>", e);
			}
			
		}
		//删除内存
		urlsMap.remove(params.get("state"));
	}
	
	
	/**
	 * 获取用户详细
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/get")
	@ResponseBody
	public JSONView get(HttpServletRequest request , @RequestBody UserModel model){
		model = userService.getUser(model);
		JSONView result = new JSONView();
		result.setReturnValue(model);
		return result;
	}
}
