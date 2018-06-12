package com.loan_utils.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class SDKHttpClient {
	static Logger logger = Logger.getLogger(SDKHttpClient.class);

	// 注册、注销
	public static String registAndLogout(String url, String param) {
		String ret = "失败";
		url = url + "?" + param;
		logger.info("【SDKHttpClient】发送请求到SDK->" + url);
		String responseString = HttpClientUtil.getInstance().doGetRequest(url);
		responseString = responseString.trim();
		if (null != responseString && !"".equals(responseString)) {
			ret = xmlResponseForRegist(responseString);
		}
		return ret;
	}

	// 解析注册注销响应
	public static String xmlResponseForRegist(String response) {
		String result = "失败";
		Document document = null;
		try {
			document = DocumentHelper.parseText(response);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		result = root.elementText("error");
		return result;
	}

	// 下发
	public static String sendSMS(String url, String param) {
		String ret = "";
		url = url + "?" + param;
		logger.info("【SDKHttpClient】发送MT到SDK->" + url);
		String responseString = HttpClientUtil.getInstance().doGetRequest(url);
		responseString = responseString.trim();
		if (null != responseString && !"".equals(responseString)) {
			ret = xmlMt(responseString);
		}
		return ret;
	}

	// 下发Post
	public static String sendSMSByPost(String url, String param) {
		String ret = "";
		url = url + "?" + param;
		logger.info("【SDKHttpClient】发送MT到SDK By Post->" + url);
		String responseString = HttpClientUtil.getInstance().doPostRequest(url);
		responseString = responseString.trim();
		if (null != responseString && !"".equals(responseString)) {
			ret = xmlMt(responseString);
		}
		return ret;
	}

	// 获取余额
	public static String getBalance(String url, String param) {
		String ret = "失败";
		url = url + "?" + param;
		logger.info("【SDKHttpClient】Balance->" + url);
		String responseString = HttpClientUtil.getInstance().doGetRequest(url);
		responseString = responseString.trim();
		if (null != responseString && !"".equals(responseString)) {
			ret = xmlResponse(responseString);
		}
		return ret;
	}

	// 统一解析格式
	public static String xmlResponse(String response) {
		String result = "失败";
		Document document = null;
		try {
			document = DocumentHelper.parseText(response);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		result = root.elementText("message");
		return result;
	}

	// 解析状态、上行
	private static List<Element> xmlDoc(String response) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(response);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		Element root = document.getRootElement();
		List<Element> list = root.elements();
		List<Element> elemets = new ArrayList<Element>();
		// 增强for循环来遍历所有的U8ArrivalVouch节点
		for (Element element : list) {
			String message = element.getName();
			if ("message".equalsIgnoreCase(message)) {
				if (element.elements().size() > 0) {
					// logger.info("--------------------"+element.elements().size());
					elemets.add(element);
				}
			}

		}
		return elemets;
	}

	// 解析下发response
	public static String xmlMt(String response) {
		String result = "0";
		Document document = null;
		try {
			document = DocumentHelper.parseText(response);
		} catch (DocumentException e) {
			e.printStackTrace();
			result = "-250";
		}
		Element root = document.getRootElement();
		result = root.elementText("error");
		if (null == result || "".equals(result)) {
			result = "-250";
		}
		return result;
	}

	public static void main(String[] args) {
		String url = "http://sdk4report.eucp.b2m.cn:8080/sdkproxy/querybalance.action";
		String param = "cdkey=6SDK-EKF-6687-KHQPL&password=795836";
		String result = SDKHttpClient.getBalance(url, param);
		logger.info(result);
	}
}
