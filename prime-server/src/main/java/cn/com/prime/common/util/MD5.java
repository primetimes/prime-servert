package cn.com.prime.common.util;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;

public  class MD5
{

	// 编码方式
    private static final String CONTENT_CHARSET = "UTF-8";
    
    private static String hexDigits[] = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
        "a", "b", "c", "d", "e", "f"
    };
    
    public static String getMD5(String sInput) throws Exception{
    	return getMD5(sInput, CONTENT_CHARSET);
    }
    
    public static String getMD5(String sInput, String charset) throws Exception{
        MessageDigest alga = MessageDigest.getInstance("MD5");       
        alga.update(sInput.getBytes(charset));        
        byte digesta[] = alga.digest();
        return byteArrayToHexString(digesta);
    }
    
    private static String byteArrayToHexString(byte b[])
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i++)
            sb.append(byteToHexString(b[i]));

        return sb.toString();
    }

    private static String byteToHexString(byte b)
    {
        int n = b;
        if(n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }


    public static void main(String a[]){
        try
        {        	
            System.out.println("--------------");
            //System.out.println(MD5.getMD5("partnerId=1000&userId=1234&userName=张三2&sessionId=asdfas234df&secret=cb7e8bbed60ae4f8eb6280585fec071f"));
            System.out.println(MD5.getMD5("哈哈", "UTF-8"));
            
            System.out.println(DigestUtils.md5Hex("哈哈".getBytes()));
            
            System.out.println(MD5.getMD5("130316"));
        }   
        catch(Exception exception) { }
    }
}
