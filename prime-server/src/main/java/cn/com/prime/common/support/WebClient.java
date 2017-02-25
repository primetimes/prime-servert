package cn.com.prime.common.support;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 封装httpClient方法
 * @author Rain
 *
 */
public class WebClient {
	
	private static Logger logger = LoggerFactory.getLogger(WebClient.class);
	public static HttpClient httpClient;
	
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
	
	
	
}
