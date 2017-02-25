package cn.com.prime.common.support;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author ming.tan@flaginfo.com.cn
 * @date 2014年11月27日 上午10:33:06
 */
public class CDataAdapter extends XmlAdapter<String, String> {

	@Override
	public String unmarshal(String v) throws Exception {
		return v;
	}

	@Override
	public String marshal(String v) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<![CDATA[").append(v).append("]]>");
		return sb.toString();
	}

}
