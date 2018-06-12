package com.loan_utils.util;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpUtil {

	private static final String HTTPS = "https://";
	protected static final int LOOP_SIZE = 5;
	private static final long SEELP_TIME = 15000;
	private static final int THREAD_SIZE = 1;
	
	private static Logger log = Logger.getLogger(HttpUtil.class);

	
	/**
	 * 通过POST方式向指定url提交指定的所有参数，并返回响应结果
	 * 
	 * @param url
	 *            url
	 * @param parameters
	 *            所有参数
	 * @return 响应结果
	 * @throws IOException
	 */
	public static String postUrlResult(String url, Map parameters) throws IOException {
		// 非http开头的url为内部或错误url,不予返回
		if(url == null || !url.startsWith("http")) {
			return null;
		}

		//https协议
		if(url.toLowerCase().indexOf(HTTPS) >= 0) {
			String param = getParameterString(parameters);
			log.info("钱包后台响应通知URL："+url+"  响应参数："+param);
			
			String content = HttpsUtil.post(url, "UTF-8", parameters);
			if(content == null) return null;
			
			log.info("PostResult phone：" + parameters.get("phone") + ",Code:200 ,Body："+ content);
			return "200";
			
//			PostMethod method = new UnsPostMethod(url, "UTF-8");
//			
//			NameValuePair[] paramList = transformParamsToNameValues(parameters);
//			if(paramList != null) {
//				// 将参数放入postMethod中
//				method.setRequestBody(paramList);
//			}
//			
//			// 执行连接
//			HttpClient client = new HttpClient();
//			client.executeMethod(method);
//			
//			String responseStr = method.getResponseBodyAsString();
//			String responseCode = String.valueOf(method.getStatusCode());
//			
//			//释放连接
//	        method.releaseConnection();
//	        log.info("PostResult MerchantId：" + parameters.get("merchantId") + ",Code:" + responseCode + " ,Body："+ responseStr);
//			return responseCode;
			
		} else {//http协议
			return IOUtil.postUrlResult(url, parameters);
		}
	}
	

	
	/**
	 * 把parameters转换成NameValuePair数组
	 * @return
	 */
	private static NameValuePair[] transformParamsToNameValues(Map parameters) {
		
		if (parameters != null && parameters.size() > 0) {
			// 把parameters转换成NameValuePair数组
			Set paramKeySet = parameters.keySet();
			NameValuePair[] paramList = new NameValuePair[paramKeySet.size()];//参数名值对
			int i = 0;
			for (Iterator iterator = paramKeySet.iterator(); iterator.hasNext();) {

				String key = (String) iterator.next();
				NameValuePair mvPair = new NameValuePair(key, (String) parameters.get(key));
				paramList[i++] = mvPair;
			}
			
			return paramList;
		}
		
		return null;
	}
	
	/**
	 * 获取所有请求参数拼凑成的参数串，形如“参数名=参数值&参数名=参数值&……”
	 * 
	 * @return 所有请求参数拼凑成的参数串
	 */
	private static String getParameterString(Map parameters) {
		String param = "";
		for (Iterator it = parameters.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			param += "&" + entry.getKey() + "=" + entry.getValue();
		}
		param = param.substring(1);
		return param;
	}
}