package cn.com.prime.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.prime.common.base.BaseController;

/**
 * 
 * @author yangchao.wang
 *	
 * controller 例子
 */
@Controller
public class SimpleDemo extends BaseController{

	/**
	 * demo
	 * @param request
	 * @return
	 */
	@RequestMapping("/demo")
	@SuppressWarnings("unchecked")
	@ResponseBody
	public Map<String, Object> doDemo(HttpServletRequest request){
		return getSuccView();
	}
	
}
