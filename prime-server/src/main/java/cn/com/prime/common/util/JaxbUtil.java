package cn.com.prime.common.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import cn.com.prime.common.support.DefaultCharacterEscapeHandler;

/**
 * @author ming.tan@flaginfo.com.cn
 * @date 2014年11月27日 上午10:42:28
 */
public class JaxbUtil {

	private static final String DEFAULT_ENCODING = "GBK";

	private static final CharacterEscapeHandler DEFAULT_CHARACTER_ESCAPE_HANDLER = new DefaultCharacterEscapeHandler();

	/**
	 * 序列化对象到xml，使用默认字符编码
	 * @param object
	 * @return
	 */
	public static <T> String toXml(T object) {
		return toXml(object, DEFAULT_ENCODING);
	}

	public static <T> String toXml(T object, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_ENCODING);
			//去掉生成xml的默认报文头。
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler",
					DEFAULT_CHARACTER_ESCAPE_HANDLER);
			StringWriter writer = new StringWriter();
			marshaller.marshal(object, writer);
			result = writer.toString();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	/**
	 * 反序列化xml为对象
	 * @param xml
	 * @param clazz
	 * @return
	 */
	public static <T> T fromXml(String xml, Class<T> clazz) {
		T result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			result = (T) unmarshaller.unmarshal(reader);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

}
