package cn.com.prime.common.exception;

/**
 * 业务异常
 * @author Perlin
 * @date 2016年5月24日
 * @since 6.0
 * @version 6.0
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String code;
	
	private String message;
	
	public BusinessException(String message) {
		this.message = message;
	}
	
	public BusinessException(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	/*@Override
	public Throwable fillInStackTrace() {
		return null;
	}*/

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
