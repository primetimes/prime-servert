package cn.com.prime.common.util;

public enum MdnCountryCode {
	
	CA(1),//加拿大
	FR(33),//法国
	DE(49),//法国
	HK(852),//香港
	IN(91),//印度
	ID(62),//印度尼西亚
	JP(81),//日本
	MO(853),//澳门
	MY(60),//马来西亚
	KR(82),//韩国
	TW(886),//台湾
	TH(66),//泰国
	GB(44),//英国
	US(1),//美国
	VN(84)//越南
	;
	
	private int code;
	
	MdnCountryCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
