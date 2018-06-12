package com.jhh.util;

public class SmsUtil {

	/**
	 * 服务http地址
	 */
	private static String BASE_URI = "http://yunpian.com";
	/**
	 * 服务版本号
	 */
	private static String VERSION = "v1";
	/**
	 * 编码格式
	 */
	private static String ENCODING = "UTF-8";
	/**
	 * 模板发送接口的http地址
	 */
	private static String URI_TPL_SEND_SMS = BASE_URI + "/" + VERSION
			+ "/sms/tpl_send.json";

	// 金互行验证码模板ID
	public final static long YMYZM_CODE = 604329;
	//
	public final static long MGYZM_CODE = 1652984;
	// 还款提醒
	public final static long MGHKTX_CODE = 1666490;
	// 放款成功
	public final static long MGFKCG_CODE = 1666486;
	// 放款拒绝
	public final static long MGFKNO_CODE = 1666488;

	public final static String apikey = "707ea5b984f33f990ed2eb100f7e1cde";

	/**
	 * 验证码短信
	 * @param tpl
	 * @param content
	 * @param mobile
	 * @return
	 */
	public static String sendSms(long tpl, String content, String mobile) {
		String result = "【悠米闪借】您的验证码是"+content;
//		String tpl_value = "#code#=" + content;
//		try {
//			HttpClient client = new HttpClient();
//			NameValuePair[] nameValuePairs = new NameValuePair[4];
//			nameValuePairs[0] = new NameValuePair("apikey", apikey);
//			nameValuePairs[1] = new NameValuePair("tpl_id", String.valueOf(tpl));// 模板id
//			nameValuePairs[2] = new NameValuePair("tpl_value", tpl_value); // 模板变量值
//			nameValuePairs[3] = new NameValuePair("mobile", mobile);
//			PostMethod method = new PostMethod(URI_TPL_SEND_SMS);
//			method.setRequestBody(nameValuePairs);
//			HttpMethodParams param = method.getParams();
//			param.setContentCharset(ENCODING);
//			client.executeMethod(method);
//			String response = method.getResponseBodyAsString();
//			System.out.println(response);
//
//			String[] arrResponse = response.split(",");
//			String[] arrResponse2 = arrResponse[0].split(":");
//			System.out.println(arrResponse2[1]);
//
//			if ("0".equals(arrResponse2[1])) {
//				result = 0;
//			} else {
//				result = 1;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return result;
	}

	/**
	 * 放款成功短信
	 * @param tpl
	 * @param realname
	 * @param money
	 * @param date
	 * @param repayment_date
	 * @param mobile
	 * @return
	 */
	public static String sendSmsFangKuanOk(long tpl, String realname,
			String money, String date, String repayment_date, String mobile) {
        // 2017.5.8更改 用户名字一律换成 用户两个字
        // String result =
        // "【悠米闪借】尊敬的"+realname+"，您申请的"+money+"、"+date+"借款已发放至您指定账户，请注意查收。请不要忘记在"+repayment_date+"及时还款。";
        String result = "【悠米闪借】尊敬的用户，您申请的" + money + "、" + date + "借 款已发放至您指定账户，请注意查收。请不要忘记在" + repayment_date
                + "及时还 款。";

//		String tpl_value = "#realname#=" + realname + "&#money#=" + money
//				+ "&#date#=" + date + "&#repayment_date#=" + repayment_date;
//		try {
//			HttpClient client = new HttpClient();
//			NameValuePair[] nameValuePairs = new NameValuePair[4];
//			nameValuePairs[0] = new NameValuePair("apikey", apikey);
//			nameValuePairs[1] = new NameValuePair("tpl_id", String.valueOf(tpl));// 模板id
//			nameValuePairs[2] = new NameValuePair("tpl_value", tpl_value); // 模板变量值
//			nameValuePairs[3] = new NameValuePair("mobile", mobile);
//			PostMethod method = new PostMethod(URI_TPL_SEND_SMS);
//			method.setRequestBody(nameValuePairs);
//			HttpMethodParams param = method.getParams();
//			param.setContentCharset(ENCODING);
//			client.executeMethod(method);
//			String response = method.getResponseBodyAsString();
//			System.out.println(response);
//
//			String[] arrResponse = response.split(",");
//			String[] arrResponse2 = arrResponse[0].split(":");
//			System.out.println(arrResponse2[1]);
//
//			if ("0".equals(arrResponse2[1])) {
//				result = 0;
//			} else {
//				result = 1;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return result;
	}
	
	/**
	 * 还款提醒短信
	 * @param tpl
	 * @param realname
	 * @param repayment_money
	 * @param repayment_date
	 * @param mobile
	 * @return
	 */
	public static String sendSmsHuanKuanTixing(long tpl, String realname,String repayment_money,
			String repayment_date, String mobile) {
        // 2017.5.8更改 用户名字一律换成 用户两个字
        // String result =
        // "【悠米闪借】尊敬的"+realname+"，您本次账单人民币"+repayment_money+"。到期还款日"+repayment_date+"。";
        String result = "【悠米闪借】尊敬的用户，您本次账单人民币" + repayment_money + "。到期回 款日" + repayment_date + "。"
				+ "http://app.youmishanjie.com/static/register/youmiRepayment.html";

//		String tpl_value = "#realname#=" + realname + "&#repayment_money#="+repayment_money+"&#repayment_date#=" + repayment_date;
//		try {
//			HttpClient client = new HttpClient();
//			NameValuePair[] nameValuePairs = new NameValuePair[4];
//			nameValuePairs[0] = new NameValuePair("apikey", apikey);
//			nameValuePairs[1] = new NameValuePair("tpl_id", String.valueOf(tpl));// 模板id
//			nameValuePairs[2] = new NameValuePair("tpl_value", tpl_value); // 模板变量值
//			nameValuePairs[3] = new NameValuePair("mobile", mobile);
//			PostMethod method = new PostMethod(URI_TPL_SEND_SMS);
//			method.setRequestBody(nameValuePairs);
//			HttpMethodParams param = method.getParams();
//			param.setContentCharset(ENCODING);
//			client.executeMethod(method);
//			String response = method.getResponseBodyAsString();
//			System.out.println(response);
//
//			String[] arrResponse = response.split(",");
//			String[] arrResponse2 = arrResponse[0].split(":");
//			System.out.println(arrResponse2[1]);
//
//			if ("0".equals(arrResponse2[1])) {
//				result = 0;
//			} else {
//				result = 1;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return result;
	}
	/**
	 * 电审不通过短信提醒
	*描述：
	*@author: wanyan
	*@date： 日期：2016年12月21日 时间：下午5:54:49
	*@param tpl
	*@param content
	*@param mobile
	*@return
	*@version 1.0
	 */
	public static String checkNoSms(long tpl, String content, String mobile) {
        // 2017.5.8更改 用户名字一律换成 用户两个字
        // String result = "【悠米闪借】尊敬的"+content+"，由于您的账户存在重大风险，发放借款失败。";
        String result = "【悠米闪借】尊敬的用户，由于您的账户存在重大风险，发放借 款失败。";
//		String tpl_value = "#realname#=" + content;
//		try {
//			HttpClient client = new HttpClient();
//			NameValuePair[] nameValuePairs = new NameValuePair[4];
//			nameValuePairs[0] = new NameValuePair("apikey", apikey);
//			nameValuePairs[1] = new NameValuePair("tpl_id", String.valueOf(tpl));// 模板id
//			nameValuePairs[2] = new NameValuePair("tpl_value", tpl_value); // 模板变量值
//			nameValuePairs[3] = new NameValuePair("mobile", mobile);
//			PostMethod method = new PostMethod(URI_TPL_SEND_SMS);
//			method.setRequestBody(nameValuePairs);
//			HttpMethodParams param = method.getParams();
//			param.setContentCharset(ENCODING);
//			client.executeMethod(method);
//			String response = method.getResponseBodyAsString();
//			System.out.println(response);
//
//			String[] arrResponse = response.split(",");
//			String[] arrResponse2 = arrResponse[0].split(":");
//			System.out.println(arrResponse2[1]);
//
//			if ("0".equals(arrResponse2[1])) {
//				result = 0;
//			} else {
//				result = 1;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return result;
	}
	
}
