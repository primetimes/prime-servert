package cn.com.prime.common.base;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import cn.com.prime.common.support.Constants;
import cn.com.prime.common.support.JSONView;
import cn.com.prime.common.support.PageInfo;
import cn.com.prime.common.support.SystemConstant;
import cn.com.prime.common.support.UserInfoHolder;
import cn.com.prime.common.util.RedisFacade;
import cn.com.prime.common.util.StringUtil;
import cn.com.prime.model.UserInfo;

/**
 * 所有业务相关的Action均继承于此类
 * 
 * @author Rain
 * 
 */
public abstract class BaseController {

	protected Logger logger = Logger.getLogger(getClass());
	
	public PageInfo pageInfo;
	
	private static final int CACHE_TIME = 30; //单位：分钟
	
	protected static final ExecutorService ex = Executors.newFixedThreadPool(20);

	protected UserInfo getUserInfo() {
		return UserInfoHolder.get();
	}
	
	protected UserInfo getUserInfo(HttpServletRequest request){
		return (UserInfo)request.getAttribute("userInfo");
	}
	
	public PageInfo getPageInfo(HttpServletRequest request){
		pageInfo = new PageInfo(request);
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	/**
	 * 绑定参数到request Attrbute
	 * 
	 * @param request
	 */
	protected void bindParamToAttrbute(HttpServletRequest request) {
		Enumeration<?> enumer = request.getParameterNames();
		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			request.setAttribute(key, request.getParameter(key));
		}
	}

	/**
	 * 绑定参数到Map
	 * 
	 * @param request
	 */
	protected Map<String, Object> bindParamToMap(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration<?> enumer = request.getParameterNames();
		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			map.put(key, request.getParameter(key));
		}
		return map;
	}
	
	
	/**
	 * 构造数据为空返回结果
	 * 
	 * @param message
	 *            数据为空返回提示
	 * @return
	 */
	protected JSONView getNullErrorView(String message) {
		JSONView JSONView = new JSONView();
		JSONView.setReturnCode(Constants.RETURN_NULL_ERROR);
		JSONView.setReturnMsg(message);
		return JSONView;
	}

	/**
	 * 构造返回数据异常结果
	 * 
	 * @param message
	 *            数据异常提示
	 * @return
	 */
	protected JSONView getDataErrorView(String message) {
		JSONView JSONView = new JSONView();
		JSONView.setReturnCode(Constants.RETURN_DATA_ERROR);
		JSONView.setReturnMsg(message);
		return JSONView;
	}

	/**
	 * 构造返回数据重复结果
	 * 
	 * @param message
	 *            数据重复提示
	 * @return
	 */
	protected JSONView getDoubleErrorView(String message) {
		JSONView JSONView = new JSONView();
		JSONView.setReturnCode(Constants.RETURN_DOUBLE_ERROR);
		JSONView.setReturnMsg(message);
		return JSONView;
	}

	/**
	 * 构造执行失败结果
	 * 
	 * @param message
	 *            执行失败提示
	 * @return
	 */
	protected JSONView getExeFailView(String message) {
		JSONView JSONView = new JSONView();
		JSONView.setReturnCode(Constants.RETURN_EXE_FAIL);
		JSONView.setReturnMsg(message);
		return JSONView;
	}
	
	protected JSONView getSuccView() {
		return getSuccView(null);
	}

	/**
	 * 构造执行成功结果
	 * 
	 * @param message
	 *            执行成功提示
	 * @return
	 */
	protected JSONView getSuccView(String message) {
		JSONView JSONView = new JSONView();
		JSONView.setReturnCode(Constants.RETURN_SUCC);
		JSONView.setReturnMsg(message == null ? "成功" : message);
		return JSONView;
	}

	/**
	 * 返回List结果
	 * 
	 * @param list
	 * @return
	 */
	protected JSONView getListView(List<?> list) {
		JSONView JSONView = new JSONView();
		JSONView.addAttribute("list", list);
		return JSONView;
	}

	/**
	 * 返回Map结果
	 * 
	 * @param map
	 * @return
	 */
	protected JSONView getMapView(Map<?, ?> map) {
		JSONView JSONView = new JSONView();
		JSONView.addAttribute("result", map);
		return JSONView;
	}

	/**
	 * 返回Map结果
	 * 
	 * @param map
	 * @return
	 */
	protected JSONView getMapViewNoResultAttr(Map map) {
		JSONView jsonView = new JSONView();
		jsonView.putAll(map);
		return jsonView;
	}

	/**
	 * 返回后台数据Map结果-若jsonServer返回的returnCode不等于200则统一返回“后台数据异常”
	 * 
	 * @param map
	 * @return
	 */
	
	/**
	 * 返回查询list的jsonView
	 * 
	 * @param result
	 * @return
	 */
	protected JSONView getSearchJSONView(Map map) {
		JSONView jsonView = new JSONView();
		jsonView.setSearchReturnType();
		if(map!=null){
			jsonView.putAll(map);
		}
		return jsonView;
	}
	
	/**
	 * 返回操作的jsonView
	 * 
	 * @param result
	 * @return
	 */
	protected JSONView getOperateJSONView(Map result) {
		JSONView jsonView = new JSONView();
		jsonView.setOperateReturnType();
		if(result!=null){
			jsonView.putAll(result);
		}
		return jsonView;
	}
	
	protected String getSpId() {
		UserInfo userInfo = UserInfoHolder.get();
		if (userInfo !=  null) {
			return userInfo.getSpId();
		}
		return null;
	}
	
	protected String getSpName() {
		UserInfo userInfo = UserInfoHolder.get();
		if (userInfo !=  null) {
			return userInfo.getSpName();
		}
		return null;
	}
	
	protected String getUserId() {
		UserInfo userInfo = UserInfoHolder.get();
		if (userInfo != null) {
			return userInfo.getUserId();
		}
		return null;
	}
	/**
	 * 根据企业编号查询省份
	 * @param spCode
	 * @return
	 */
	protected String getPlatform() {
		UserInfo userInfo = UserInfoHolder.get();
		if (userInfo != null) {
			return userInfo.getPlatform();
		}
		return null;
	}
	
	/**
	 * 判断重复提交
	 * @param serialNumber
	 * @param requestMap
	 * @return
	 * @throws Exception 
	 */
	protected boolean isReSubmit(String serialNumber, String spId, String userId, String keyPrefix) {
		boolean isResubmit = false;
		String redisKey = keyPrefix + spId + "_" + userId;
		String cacheSerialNo = RedisFacade.getSet(redisKey, serialNumber);
		RedisFacade.expire(redisKey, CACHE_TIME * 60);
		//如果老的单号跟新单号相同，则为重复提交
		if(!StringUtil.isNullOrEmpty(cacheSerialNo) && cacheSerialNo.equals(serialNumber)) {
			isResubmit = true;
		}
		/*Lock lock = null;
		try {
			//lock = RedisLock.getLock(redisKey + "_LOCK", 1000L);
			String cacheSerialNo = RedisFacade.get(redisKey);
			if (StringUtil.isNullOrEmpty(cacheSerialNo)) {
				//将序列号放到redis中
				RedisFacade.set(redisKey, serialNumber, CACHE_TIME, TimeUnit.MINUTES);
			} else {
				if (cacheSerialNo.equals(serialNumber)) {
					isResubmit = true;
				} else {
					RedisFacade.set(redisKey, serialNumber, CACHE_TIME, TimeUnit.MINUTES);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (lock != null) {
				lock.releaseLock();
			}
		}*/
		return isResubmit;
	}
	
	
	/**
	 * 判断结果是否为成功
	 * @param map
	 * @return
	 */
	public boolean isSuccResult(Map<String, Object> map){
		if(!StringUtil.isEmptyInMap(map, SystemConstant.KEY_RETURN_CODE) 
				&& map.get(SystemConstant.KEY_RETURN_CODE).equals(SystemConstant.RETURN_SUCC)){
			return true;
		}
		return false;
	}
	
	/**
	 * 服务调用是否有返回的错误码
	 * @param map
	 * @return
	 */
	public boolean isSuccServiceCall(Map<String, Object> map){
		if(!StringUtil.isEmptyInMap(map, SystemConstant.KEY_SERVICE_ERROR_CODE)){
			return false;
		}
		return true;
	}
}
