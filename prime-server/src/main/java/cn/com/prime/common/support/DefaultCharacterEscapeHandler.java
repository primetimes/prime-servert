package cn.com.prime.common.support;

import java.io.IOException;
import java.io.Writer;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

/**
 * @author ming.tan@flaginfo.com.cn
 * @date 2014年11月27日 上午11:18:40
 */
public class DefaultCharacterEscapeHandler implements CharacterEscapeHandler {

	@Override
	public void escape(char[] ch, int start, int length, boolean flag, Writer writer) throws IOException {
		writer.write(ch, start, length);
	}

}
