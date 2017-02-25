package cn.com.prime.common.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

public class ConnectionFactory {

	private static ConnectionFactory factory ;
	
	public synchronized static ConnectionFactory getInstance(){
		if(factory==null){
			factory = new ConnectionFactory();
			/*try {
				JAXPConfigurator.configure("WEB-INF/ProxoolConf.xml", false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		return factory;
	}
	
	public Connection getConnection(String dbname){
		Connection conn = null;
		try {
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			conn = DriverManager.getConnection("proxool." + dbname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	public static void main(String[] args) {
		Connection conn = null;
		try {
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			JAXPConfigurator.configure("WEB-INF/ProxoolConf.xml", false);
			conn = DriverManager.getConnection("proxool.db_zx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
