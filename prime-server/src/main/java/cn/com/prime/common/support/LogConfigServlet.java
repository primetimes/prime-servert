package cn.com.prime.common.support;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

/**
 * 日志配置加载
 * @author Rain
 *
 */
public class LogConfigServlet extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	
	private String logPath = LogConfigServlet.class.getResource("/").getPath()+File.separator+"log4j.properties";
	
	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("logPath:"+logPath);
		new ReloadThread().start();
	}
	
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	class ReloadThread extends Thread{
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(10000);
					reload();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		public void reload() throws Exception{
			Properties p = new Properties();
			p.load(new FileInputStream(new File(logPath)));
			PropertyConfigurator.configure(p);
		}
		
	}
	

}
