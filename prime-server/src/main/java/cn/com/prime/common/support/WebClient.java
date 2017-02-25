package cn.com.prime.common.support;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cn.com.prime.common.util.StringUtil;

/**
 * 封装httpClient方法
 * 具体可继承
 * @see FormClient
 * @author Rain
 *
 */
@Deprecated
public class WebClient {
	
	private static Logger logger = LoggerFactory.getLogger(WebClient.class);
	public static HttpClient httpClient;
	
	private Map<String,String> headerMap;
	
	
	static{
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager(); 
		HttpConnectionManagerParams params = connectionManager.getParams();
        params.setConnectionTimeout(60000); 
        params.setSoTimeout(120000); 
        params.setDefaultMaxConnectionsPerHost(32);
        params.setMaxTotalConnections(256);
		httpClient = new HttpClient(connectionManager);
        logger.info("httpclient init finished!");
	}
	
	
	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
	}
	
	public Map<String, String> getHeaderMap() {
		return headerMap;
	}
	
	/**
	 * postForm返回Map<br/>
	 * 默认接口返回值的为Json格式的字符串
	 * @param url
	 * @param formParams
	 * @return
	 */
	public Map<String,Object> postFormToMap(String url,Map<String,String> formParams){
		String result = this.postForm(url, formParams);
		if(StringUtil.isNullOrEmpty(result)){
			return null;
		}
		return JsonHelper.parseToMap(result);
		
	}
	
	/**
	 * POST JSON
	 * @param url
	 * @param params
	 * @return
	 */
	public String postJson(String url,Map<String,Object> params){
		return this.postJson(url,JsonHelper.parseToJson(params));
	}
	
	/**
     * POST JSON返回Map结果
     * @param url
     * @param params
     * @return
     */
    public Map postJsonReturnMap(String url,Map<String,Object> params){
         return this.postJsonReturnMap(url,JsonHelper.parseToJson(params));
    }
    
    /**
     * POST JSON返回Map结果
     * @param url
     * @param params
     * @return
     */
    public Map postJsonReturnMap(String url,String params){
         return JsonHelper.parseToMap(this.postJson(url,params));
    }
    
    
    public String postJson(String url,String json){
        
        PostMethod post = new PostMethod(url);
        try {
            RequestEntity entity = new StringRequestEntity(json,"application/json","UTF-8");
            post.setRequestEntity(entity);
            if(null != headerMap&&headerMap.size()>0) {
            for(String key : headerMap.keySet()){
            	post.addRequestHeader(key, headerMap.get(key));
            }
            }
            int status = httpClient.executeMethod(post);
            if(status!=HttpStatus.SC_OK){
                throw new RuntimeException("调用服务异常,状态："+status);
            }
            String response = getContent(post.getResponseHeader("Content-Encoding") == null ? null:post.getResponseHeader("Content-Encoding").getValue(),
                    post.getResponseBodyAsStream());
            
            logger.info("post Url:{},Json:{},response:{} ",url,json,response);
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("调用服务异常",e);
        }finally{
            post.releaseConnection();
        }
        
    }
	public String getQueryString(Map<String,String> reqMap){
		if(reqMap == null || reqMap.isEmpty()){
			return null;
		}
		Set<String> set = reqMap.keySet();
		StringBuffer queryString = new StringBuffer();
		boolean isFirst = true;
		for(String k:set){
			String value = reqMap.get(k);
			if(k==null){
				k = "";
			}
			if(isFirst){
				isFirst = false;
			}else{
				queryString.append("&");
			}
			queryString.append(k).append("=").append(value);
		}
		return queryString.toString();
	}
	


	public String get(String url,String queryString){
		
		logger.info(" get url:"+url +" params: {}",queryString);
		if(url.contains("?")){
			url = url+"&"+queryString;
		}else{
			url = url+"?"+queryString;
		}
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		try {
			if(headerMap != null && !headerMap.isEmpty()){
				Set<Entry<String, String>> entrySet = headerMap.entrySet();
				for(Entry<String,String> e:entrySet){
					method.addRequestHeader(e.getKey(),e.getValue());
				}
			}
			httpClient.executeMethod(method);
			String encoding = method.getResponseHeader("Content-Encoding")==null ? 
								null:method.getResponseHeader("Content-Encoding").getValue();
			String response = getContent(encoding,method.getResponseBodyAsStream());
			logger.info("get:"+response.toString());
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			method.releaseConnection();
		}
		
		return null;
	}
	
	/**
	 * POST Form
	 * @param url
	 * @param formParams
	 * @return
	 */
	public String postForm(String url,Map<String,String> formParams){
		
		PostMethod post=new PostMethod(url);
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		logger.info(" post url:"+url+" params:"+formParams);
		try {
			if(headerMap != null && !headerMap.isEmpty()){
				Set<Entry<String, String>> entrySet = headerMap.entrySet();
				for(Entry<String,String> e:entrySet){
					post.addRequestHeader(e.getKey(),e.getValue());
				}
			}
			if(formParams != null){
				Set<String> keys = formParams.keySet();
				for(String key:keys){
					String value = formParams.get(key);
					if(!StringUtil.isNullOrEmpty(value)){
						post.setParameter(key, value);
					}
				}
			}
			int status= httpClient.executeMethod(post);
			StringBuffer strBuff = new StringBuffer();
			if(status!=HttpStatus.SC_OK){
				logger.info("post失败:"+status);
				return strBuff.toString();
			}
			BufferedReader reader=new BufferedReader(new InputStreamReader(post.getResponseBodyAsStream()));
			String str="";
			while((str=reader.readLine())!=null){
				strBuff.append(str);
			}
			logger.info("postForm:"+strBuff.toString());
			return strBuff.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(post!=null){
				post.releaseConnection();
			}
		}
		return null;
	}
	
	/**
	 * 上传文件
	 * @param url
	 * @param reqForm<String,Object> object支持String、File 
	 * @return
	 */
	public String postUpload(String url,Map<String,Object> reqForm){
		
		List<Part> list = new ArrayList<Part>();
		Set<String> set = reqForm.keySet();
		for(String key:set){
			Object o = reqForm.get(key);
			if(o == null){
				continue;
			}
			Part part = null;
			if(o instanceof File){
				try {
					part = new FilePart(key,(File)o);
				} catch (FileNotFoundException e) {
					logger.info("file:"+((File)o).getAbsolutePath()+" don't exists ");
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}else{
				part = new StringPart(key,String.valueOf(o));
			}
			list.add(part);
		}
		
		Part[] parts = list.toArray(new Part[list.size()]);
		
		return this.upload(url, parts);
	}
	
	
	/**
	 * 上传文件
	 * @param url 文件地址
	 * @param parts 文件
	 * @return
	 */
	public String upload(String url,Part[] parts){
		
		PostMethod filePost=new PostMethod(url);//上传地址
		try {
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			int status=httpClient.executeMethod(filePost);
			logger.info("status:"+filePost.getStatusLine());
			StringBuffer strBuff = new StringBuffer();
			if(status!=HttpStatus.SC_OK){
				logger.info("上传文件失败:"+status);
				return strBuff.toString();
			}
			BufferedReader reader=new BufferedReader(new InputStreamReader(filePost.getResponseBodyAsStream()));
			String str="";
			while((str=reader.readLine())!=null){
				strBuff.append(str);
			}
			logger.info("upload:"+strBuff.toString());
			return strBuff.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(filePost!=null){
				filePost.releaseConnection();
			}
		}
		return null;
	}
	
	public static HttpClient getHttpClient() {
		return httpClient;
	}

	public static void setHttpClient(HttpClient httpClient) {
		WebClient.httpClient = httpClient;
	}

	/**
	 * 获取输入流中的字符串
	 * @param is  输入流
	 * @return
	 */
	private String getContent(String contentEncoding, InputStream in) throws IOException{
		String answer = null;
		if ( in != null ){
			try{
				if ( contentEncoding != null && contentEncoding.equals( "gzip" ) ){
					byte[] b = null;
					GZIPInputStream gzip = new GZIPInputStream( in );
					byte[] buffer = new byte[1024];
					int num = -1;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					while ( (num = gzip.read( buffer ,0 ,buffer.length )) != -1 ){
						baos.write( buffer ,0 ,num );
					}
					b = baos.toByteArray();
					baos.flush();
					baos.close();
					gzip.close();
					answer = new String( b, "utf-8" ).trim();
					buffer = null;
				}else{
					InputStreamReader inr = new InputStreamReader( in, "utf-8");
					BufferedReader br = new BufferedReader( inr );
					String line = null;
					StringBuffer sb = new StringBuffer();
					while ( (line = br.readLine()) != null ){
						sb.append( line ).append( "\n" );
					}
					answer = sb.toString();
				}
			}
			finally{
				in.close();
			}
		}
		return answer;
	}
	
	
	
}
