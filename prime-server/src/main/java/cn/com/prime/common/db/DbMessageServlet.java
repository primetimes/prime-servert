package cn.com.prime.common.db;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.admin.SnapshotIF;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;

import cn.com.prime.common.util.SystemMessage;

public class DbMessageServlet extends HttpServlet{

	public static final String DATAID = SystemMessage.getString("db_data_id");
	public static final String GROUP = SystemMessage.getString("db_group");
	private static final String XML_FILE_PROPERTY = "xmlFile";
	private static Logger logger = LoggerFactory.getLogger(DbMessageServlet.class);
	private static String INIT_CONF;
	
	
	public void init(ServletConfig servletConfig) throws ServletException {
		
        super.init(servletConfig);
        
        if("1".equals(SystemMessage.getString("local_conf"))){
        	logger.info("----------------------------------------------");
			logger.info("----开启本地读取模式，读取本地配置system--------");
			logger.info("----------------------------------------------");
        	String appDir = servletConfig.getServletContext().getRealPath("/");
            Enumeration names = servletConfig.getInitParameterNames();
        	while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                String value = servletConfig.getInitParameter(name);
                if (name.equals(XML_FILE_PROPERTY)) {
                    try {
                        File file = new File(value);
                        if (file.isAbsolute()) {
                            JAXPConfigurator.configure(value, false);
                        } else {
                            JAXPConfigurator.configure(appDir + File.separator + value, false);
                        }
                    } catch (ProxoolException e) {
                        e.printStackTrace();
                    }
                } 
            }
        }
        
        //定时扫描连接池数量
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		final Runnable command = new Runnable() {
			@Override
			public void run() {
				try {
					String dbAlias[] = ProxoolFacade.getAliases();
					for(String alias:dbAlias){
						SnapshotIF snapshot = ProxoolFacade.getSnapshot(alias);
						
						logger.info("proxool snapshot info,name="+alias+" active:"+snapshot.getActiveConnectionCount()
								+" available:"+snapshot.getAvailableConnectionCount()
								+" total:"+snapshot.getMaximumConnectionCount());
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("...get proxool exception...");
				}
			}
		};
		
		//10m扫描一次
		executor.scheduleAtFixedRate(command, 180, 600, TimeUnit.SECONDS);
        
    }
	
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String dbAlias[] = ProxoolFacade.getAliases();
		PrintWriter pw = resp.getWriter();
		try {
			pw.write("proxool pool info:\n");
			for(String alias:dbAlias){
				SnapshotIF snapshot = ProxoolFacade.getSnapshot(alias);
				pw.write("name="+alias+"\t active:"+snapshot.getActiveConnectionCount()
						+" available:"+snapshot.getAvailableConnectionCount()
						+" total:"+snapshot.getMaximumConnectionCount()+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		
		
	}

	
	class ProxoolManagerListener implements ManagerListener{
		
		private int updateCount = 0;
		@Override
		public void receiveConfigInfo(String config) {
			logger.info("///////////////////////////////receiveConfigInfo///////////////////////");
			if(INIT_CONF==null || !INIT_CONF.equals(config)){
				updateCount++;
				logger.info("****************update proxool config count:"+updateCount+"********************");
				ProxoolFacade.shutdown(0);
				configure(config);
			}
		}
		@Override
		public Executor getExecutor() {
			return null;
		}
	}
	
	
	private static void configure(String xml){
		try {
			logger.info("-----------解析数据库配置文件----------------");
			logger.info("-----------"+xml+"----------------");
			logger.info("-----------解析数据库配置文件----------------");
			StringReader r = new StringReader(xml);
			JAXPConfigurator.configure(r, false);
			r.close();
		} catch (ProxoolException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
