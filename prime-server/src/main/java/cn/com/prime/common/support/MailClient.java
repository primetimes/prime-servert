package cn.com.prime.common.support;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import cn.com.prime.common.util.SystemMessage;

public class MailClient {
	private static Properties properies = new Properties();
	private static Logger logger = Logger.getLogger(MailClient.class);
	
	static{
		properies.put("mail.smtp.auth",SystemMessage.getString("im_mail.smtp.auth"));
		properies.put("mail.transport.protocol",SystemMessage.getString("im_mail.transport.protocol"));
		properies.put("mail.smtp.host", SystemMessage.getString("im_mail.smtp.host"));  
		properies.put("mail.smtp.port", SystemMessage.getString("im_mail.smtp.port"));  
		logger.info(properies);
	}
	
	
	/**
	 * 多个地址群发
	 * 如果地址无效，选择有效的地址再发送
	 * @param toEmails
	 * @param subject
	 * @param body
	 * @return
	 */
	public boolean sendEmail(List<String> toEmails,String subject,String body){
		if(toEmails==null || toEmails.size()==0){
			return false;
		}
		//建立会话  
        Session session = Session.getInstance(properies);  
        Message msg = new MimeMessage(session); //建立信息  
        Transport tran = null;
        
        try {
			msg.setFrom(new InternetAddress(SystemMessage.getString("im_mail.sender.email"))); //发件人  
			
			InternetAddress [] toAddresses = new InternetAddress[toEmails.size()];
			for(int i=0;i<toEmails.size();i++){
				toAddresses[i] = new InternetAddress(toEmails.get(i)); 
			}
			
			msg.setRecipients(Message.RecipientType.TO,toAddresses);
			msg.setSentDate(new Date()); // 发送日期  
			msg.setSubject(subject); // 主题  
			msg.setText(body); //内容
			
			// 邮件服务器进行验证  
			try {
				tran = session.getTransport("smtp"); 
				tran.connect(properies.getProperty("host"), 
						SystemMessage.getString("im_mail.sender.username"), 
						SystemMessage.getString("im_mail.sender.password"));  
				  
				tran.sendMessage(msg, msg.getAllRecipients()); // 发送  
				
			} catch (SendFailedException  se) { //存在无效地址,再发送一次
				Address []validAddress = se.getValidSentAddresses();
				if(validAddress!=null && toAddresses.length>0){
					msg.setRecipients(Message.RecipientType.TO,toAddresses);
					tran.sendMessage(msg, msg.getAllRecipients()); // 发送  
				}
			}
			logger.debug("邮件发送成功");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("发送邮件失败..."+e.getMessage());
		} 
		
		return false;
	}
	
	
	public boolean sendEmail(String toEmails[],String subject,String body){
        return sendEmail(Arrays.asList(toEmails), subject, body);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		MailClient client = new MailClient();
		client.sendEmail(new String[]{"erping.zhou@flaginfo.com.cn"},"test email 测试发送","test email body...........................  dddd Cookie	JSESSIONID=7A199DBC349E757ECF1BC723F85A0E9BCookie	JSESSIONID=7A199DBC349E757ECF1BC723F85A0E9BCookie	JSESSIONID=7A199DBC349E757ECF1BC723F85A0E9B");
//		try {  
////	           Properties p = new Properties(); //Properties p = System.getProperties();  
////	            p.put("mail.smtp.auth", "true");  
////	            p.put("mail.transport.protocol", "smtp");  
////	            p.put("mail.smtp.host", "smtp.163.com");  
////	            p.put("mail.smtp.port", "25");  
//	            //建立会话  
//	            Session session = Session.getInstance(properies);  
//	            Message msg = new MimeMessage(session); //建立信息  
//	   
//	            msg.setFrom(new InternetAddress("rain_pingzh@163.com")); //发件人  
//	            msg.setRecipient(Message.RecipientType.TO,  
//	                             new InternetAddress("erping.zhou@flaginfo.com.cn")); //收件人  
//	   
//	            msg.setSentDate(new Date()); // 发送日期  
//	            msg.setSubject("答话稀有"); // 主题  
//	            msg.setText("快点下在"); //内容  
//	            // 邮件服务器进行验证  
//	            Transport tran = session.getTransport("smtp");  
//	            tran.connect("smtp.163.com", "rain_pingzh", "rain850513");  
//	            // bluebit_cn是用户名，xiaohao是密码  
//	            tran.sendMessage(msg, msg.getAllRecipients()); // 发送  
//	            System.out.println("邮件发送成功");  
//	   
//	        } catch (Exception e) {  
//	            e.printStackTrace();  
//	        }  
	
	}

}
