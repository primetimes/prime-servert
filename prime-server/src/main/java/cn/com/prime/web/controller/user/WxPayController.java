package cn.com.prime.web.controller.user;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.prime.common.base.BaseController;
import cn.com.prime.common.util.StringUtil;
import cn.com.prime.service.user.UserService;
/**
 * 用户管理控制器
 * @author yangchao.wang
 *	2016年12月6日
 */
@Controller
@RequestMapping("/user")
public class WxPayController extends BaseController{

	@Autowired
	private UserService  userService;
	
	
	
	/**
	 * 获取微信用户详细
	 * @param request
	 */
	@RequestMapping(value = "/wx/get-detail")
	public void queryOrder(HttpServletRequest request, HttpServletResponse response){
		
		Map<String, Object> params = this.bindParamToMap(request);
		//获取跳转页面
		String redirectUrl = StringUtil.getStringInMap("redirectUrl", params);
		if(StringUtil.isNullOrEmpty(redirectUrl)){
			throw new RuntimeException("redirectUrl = null");
		}
		
		
		
		
		
	}
	
	
}
