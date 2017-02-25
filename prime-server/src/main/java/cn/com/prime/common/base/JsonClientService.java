package cn.com.prime.common.base;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import cn.com.prime.common.support.ErrorConstants;
import cn.com.prime.common.support.JsonHelper;
import cn.com.prime.common.support.PaymentToolsFacade;
import cn.com.prime.common.support.SystemConstant;
import cn.com.prime.common.support.UserInfoHolder;
import cn.com.prime.common.util.SystemMessage;
import cn.com.prime.model.UserInfo;

import com.sun.jersey.api.client.Client;

/**
 * 基础服务
 * 所有的service都需要继承他
 * @author Rain
 *
 */
public abstract class JsonClientService {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private Client jsonClient;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public Client getJsonClient() {
		return jsonClient;
	}
	public void setJsonClient(Client jsonClient) {
		this.jsonClient = jsonClient;
	}
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * 微信http请求模板
	 */
	@Autowired
	protected RestTemplate template;

	public RestTemplate getTemplate() {
		return template;
	}

	public void setTemplate(RestTemplate template) {
		this.template = template;
	}

	public <K, V> Map<K, V> newHashMap() {
		Map<K, V> map = new HashMap<K, V>();
		return map;
	}

	public <K, V> Map<K, V> newTreeMap() {
		Map<K, V> map = new TreeMap<K, V>();
		return map;
	}

	public <T> List<T> newArrayList() {
		List<T> list = new ArrayList<T>();
		return list;
	}

	public <T> List<T> newLinkedList() {
		List<T> list = new LinkedList<T>();
		return list;
	}
	
	public  Map<String, Object> transBean2Map(Object obj) {  
		  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<String, Object>();  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
                    if(value == null){
                    	continue;
                    }
                    map.put(key, value);  
                }  
  
            }  
        } catch (Exception e) {  
        	e.printStackTrace();
        	throw new RuntimeException("bean2map 异常!");
        }  
  
        return map;  
  
    }  
	
	/**
	 * 检查返回数据签名是否正常
	 * @param xml 请求的字符串信息
	 * @param merKey 商户密钥
	 * @return
	 */
	public boolean checkedPaymentResponseSign(String xml,String merKey){
		return PaymentToolsFacade.checkedSignByResponseXML(xml, merKey);
	}
	
	/**
	 * HTTP GET请求
	 * @param url
	 * @param requestMap
	 * @param responseClass
	 * @return
	 * @throws IOException
	 */
	public <T> T getForObject(String url, Map<String, Object> requestMap, Class<T> responseClass) {
		T result = null;
		logger.info("request url="+url+";request params"+requestMap);
		String resp = null;
		try {
			String path = url + "?" + makeParameter(requestMap);
			URL reqUrl = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) reqUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.connect();
			int code = conn.getResponseCode();
			resp = null;
			if (code == 200) {
				InputStream is = null;
				String contentEncoding = conn.getHeaderField("Content-Encoding");
				if (contentEncoding != null && contentEncoding.equals("gzip")) {
					is = new GZIPInputStream(conn.getInputStream());
				} else {
					is = conn.getInputStream();
				}
				resp = getContent(is);
				is.close();
			} else {
				InputStream error = conn.getErrorStream();
				throw new IOException(getContent(error));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
 		if (resp != null) {
 			result = JsonHelper.parseToObject(resp, responseClass);
 		}
 		return result;
	}
	
	/**
	 * 根据参数生成url访问参数（不包括地址，只有queryString）
	 * @param parameter 请求参数
	 * @return 参数字符串（不包括地址，只有queryString）
	 */
	private String makeParameter(Map<?, ?> parameter) {
		StringBuilder answer = new StringBuilder();
		boolean bool = false;
		for (Map.Entry<?, ?> en : parameter.entrySet()) {
			if (bool) {
				answer.append("&");
			} else {
				bool = true;
			}
			answer.append(en.getKey()).append("=").append(en.getValue());
		}
		return answer.toString();
	}
	
	/**
	 * 获取输入流中的字符串
	 * @param is  输入流
	 * @return
	 */
	private String getContent(InputStream is) throws IOException {
		String answer = null;
		if (is != null) {
			InputStreamReader inr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(inr);
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			answer = sb.toString();
		}
		return answer;
	}
	
	/**
	 * 通用的参数
	 * @param url
	 * @param requestMap
	 * @param responseClass
	 * @return
	 */
	public <T> T postForObject(String url,Map<String,Object> requestMap,Class<T> responseClass){
		return postForObject(url,requestMap,responseClass,true);
	}
	
	/**
	 * post server
	 * @param url
	 * @param requestMap
	 * @param clazz
	 * @param isUserAuth 是否加入用户信息
	 * @return
	 */
	public <T> T postForObject(String url,Map<String,Object> requestMap,Class<T> responseClass,boolean isUserAuth){
		T result = null;
		logger.info("request url="+url+";request params"+requestMap);
		if(isUserAuth){
			UserInfo userInfo = getUserInfo();
			Map<String,Object> userMap = new HashMap<String,Object>(); //不需要把所有的字段都传到后台
			userMap.put("ip",userInfo.getIp());
			userMap.put("spId",userInfo.getSpId());
			userMap.put("userId",userInfo.getUserId());
			//userMap.put("userName",userInfo.getUserName());
			HttpHeaders headers = new HttpHeaders();
			try {
				headers.add("sessionUser",URLEncoder.encode(JsonHelper.parseToJson(userMap),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			HttpEntity request= new HttpEntity(requestMap, headers);
			result = getRestTemplate().postForObject(url, request, responseClass);
			return result;
			
		}else{
			result = getRestTemplate().postForObject(url, requestMap, responseClass);
		}
		
		return result;
	}
	
	/**
	 * 获取用户信息
	 * @return
	 */
	public UserInfo getUserInfo(){
		return UserInfoHolder.get();
	}
	


	
	/**
	 * 返回原Map中的部分数据
	 * @param srcMap
	 * @param keys
	 * @return
	 */
	protected Map<String, Object> sliceMap(Map<String, Object>srcMap, String... keys) {
		Map<String, Object> sliceMap = new HashMap<String, Object>();
		for (String key : keys) {
			sliceMap.put(key, srcMap.get(key));
		}
		return sliceMap;
	}
	

	
	
	/**
	 * 获取错误返回信息
	 * @param errorCode
	 * @param errorMsg
	 * @return
	 */
	public Map<String, Object> getErrInfo(String errorCode, String errorMsg){
		Map<String, Object> result = this.newHashMap();
		result.put(SystemConstant.KEY_SERVICE_ERROR_CODE, errorCode);
		result.put(SystemConstant.KEY_SERVICE_ERROR_MSG, errorMsg);
		return result;
	}
}
