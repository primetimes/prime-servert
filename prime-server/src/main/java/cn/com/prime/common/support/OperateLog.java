package cn.com.prime.common.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 
 * <pre>
 * 操作日志注解，定义在BaseJson的子类的方法的签名处。
 * 注解中需要定义:
 * (1) 日志类型： type，即im_operate_type 表中定义的EP需要记录的日志类型，支持动态参数,如：type = "${logType}"
 * (2) 日志消息： message,可以带参数，如：${spId},参数必须出现在BaseJson
 * 子类的方法返回字符串或者方法参数字符串中。
 * (3) 执行失败日志消息： error,可以带参数，如：${spId},参数必须出现在BaseJson
 * 子类的方法返回字符串或者方法参数字符串中。
 * 
 * 示例：
 * @OperateLog(type = 25,message="企业用户：${userName}登陆")
 * @POST
 *	public String login(String content) throws Exception {
 *		Map<String, String> params = JsonHelper.parseToMap(content);
 *      Map<String, String> result = newHashMap();
 *      result.put("userName","testSP");
 *      result.put("spId","spId");
 *		return getMapView(result);
 *	}
 * </pre>
 * 
 * @author ming.tan@flaginfo.com.cn
 * @date 2014年11月5日 下午1:35:49
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
	/**
	 * 操作日志类型 （type） 代表的含义如下 ： 
	 * 
	 * <ul>
	 * <li>4	添加模板         </li>	
	 * <li>5	编辑模板	     </li>
	 * <li>6	删除模板	     </li>
	 * <li>16	创建信息	     </li>
	 * <li>17	编辑信息	     </li>
	 * <li>18	删除信息	     </li>
	 * <li>25	企业用户登录	 </li>
	 * <li>26	企业用户退出	 </li>
	 * <li>29	客户信息导入	 </li>
	 * <li>36	复制信息	     </li>
	 * <li>37	添加问题	     </li>
	 * <li>38	删除问题	     </li>
	 * <li>39	编辑问题	     </li>
	 * <li>40	添加素材类别	 </li>
	 * <li>41	删除素材类别	 </li>
	 * <li>42	编辑素材类别	 </li>
	 * <li>43	添加素材	     </li>
	 * <li>44	删除素材	     </li>
	 * <li>45	编辑素材	     </li>
	 * <li>53	添加管理员组	 </li>
	 * <li>54	编辑管理员组	 </li>
	 * <li>55	删除管理员组	 </li>
	 * <li>56	添加客户群组	 </li>
	 * <li>57	删除客户群组	 </li>
	 * <li>58	编辑客户群组	 </li>
	 * <li>59	添加客户	     </li>
	 * <li>60	删除客户	     </li>
	 * <li>61	编辑客户	     </li>
	 * <li>63	添加信息类型	 </li>
	 * <li>64	客户导入群组	 </li>
	 * <li>65	删除信息类型	 </li>
	 * <li>66	编辑信息类型	 </li>
	 * <li>75	发送信息	     </li>
	 * <li>80	添加优惠券	 </li>
	 * <li>81	修改优惠券	 </li>
	 * <li>82	删除优惠券	 </li>
	 * <li>83	添加验证终端	 </li>
	 * <li>84	修改验证终端	 </li>
	 * <li>85	删除验证终端	 </li>
	 * <li>86	发送验证	     </li>
	 * <li>87	修改积分	     </li>
	 * <li>88	添加个人通讯录 </li>
	 * <li>89	修改个人通讯录</li>
	 * <li>90	删除个人通讯录</li>
	 * <li>91	添加个人群组	 </li>
	 * <li>92	修改个人群组	 </li>
	 * <li>93	删除个人群组	 </li>
	 * <li>94	个人导入群组	 </li>
	 * <li>95	添加投票主题	 </li>
	 * <li>96	修改投票主题	 </li>
	 * <li>97	删除投票主题	 </li>
	 * <li>98	投票设置	     </li>
	 * <li>99	投票数设置	 </li>
	 * <li>100	添加投票有效号码 </li>	
	 * <li>101	删除投票有效号码 </li>	
	 * <li>102	导入有效投票号码 </li>	
	 * <li>103	添加短语类别	 </li>
	 * <li>104	删除短语类别	 </li>
	 * <li>105	编辑短语类别	 </li>
	 * <li>106	添加短语	     </li>
	 * <li>107	删除短语	     </li>
	 * <li>108	编辑短语	     </li>
	 * <li>109	添加文件类别	 </li>
	 * <li>110	删除文件类别	 </li>
	 * <li>111	编辑文件类别	 </li>
	 * <li>112	添加文件	     </li>
	 * <li>113	删除文件	     </li>
	 * <li>114	编辑文件	     </li>
	 * <li>115	添加E信点播	 </li>
	 * <li>116	删除E信点播	 </li>
	 * <li>117	编辑E信点播	 </li>
	 * <li>118	添加短信点播     </li>
	 * <li>119	删除短信点播     </li>
	 * <li>120	编辑短信点播     </li>
	 * <li>124	添加操作员	 </li>
	 * <li>125	删除操作员	 </li>
	 * <li>126	修改操作员	 </li>
	 * <li>133	清空投票信息	 </li>
	 * </ul>
	 * @return
	 */
	String type() default "";

	/**
	 * 操作执行成功时的日志消息，可以包含参数例如： ${spId}
	 * @return
	 */
	String message() default "";

	/**
	 * 操作执行失败时的日志消息，可以包含参数
	 * @return
	 */
	String error() default "";
}
