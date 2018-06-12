package com.loan_utils.util;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jianglei
 * 
 */
public class IOUtil {
	
	private static Logger log = Logger.getLogger(IOUtil.class);
	
	private IOUtil() {
	}

	/**
	 * 获取指定url中的内容
	 * 
	 * @see URLConnection.getContent()
	 */
	public static Object getUrlContent(String url) throws IOException {
		URLConnection connection = new URL(url).openConnection();
		connection.connect();
		return connection.getContent();
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
	public static String postUrl(String url, Map parameters) throws IOException {
		// 商户不返回merchantKey
		parameters.remove("merchantKey");
		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		if (parameters != null) {
			writer.write(getParameterString(parameters));
		}
		writer.flush();
		writer.close();

		// zln 修正 20080407 begin 修正读取反馈页面会出现sun.io.MalformedInputException的问题
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		StringBuffer sBuffer = new StringBuffer();
		String tempStr;
		byte[] b = new byte[1024];
		int i = 0;
		while ((i = bis.read(b)) > 0) {
			tempStr = new String(b, 0, i, "GB2312");
			sBuffer.append(tempStr);
		}
		// zln 修正 20080407 end

		return sBuffer.toString();
	}
	/**
	 * 通过POST方式向指定url提交指定的所有参数，并返回响应结果
	 * 
	 * @param url
	 *            url
	 * @param parameters
	 *            所有参数
	 * @param unicode
	 *          编码格式           
	 * @return 响应结果
	 * @throws IOException
	 */
	public static String postUrl(String url, Map parameters,String unicode) throws IOException {
		// 商户不返回merchantKey
		parameters.remove("merchantKey");
		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		if (parameters != null) {
			writer.write(getParameterString(parameters));
		}
		writer.flush();
		writer.close();

		// zln 修正 20080407 begin 修正读取反馈页面会出现sun.io.MalformedInputException的问题
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		StringBuffer sBuffer = new StringBuffer();
		String tempStr;
		byte[] b = new byte[1024];
		int i = 0;
		while ((i = bis.read(b)) > 0) {
			tempStr = new String(b, 0, i, unicode);
			sBuffer.append(tempStr);
		}
		// zln 修正 20080407 end

		return sBuffer.toString();
	}
	
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
		URL surl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) surl.openConnection();
		connection.setConnectTimeout(30000);
		String param = getParameterString(parameters);
		log.info("响应通知URL："+url+"  响应参数："+param);
		connection.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		if (parameters != null) {
			writer.write(getParameterString(parameters));
		}
		writer.flush();
		writer.close();

		// zln 修正 20080407 begin 修正读取反馈页面会出现sun.io.MalformedInputException的问题
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		StringBuffer sBuffer = new StringBuffer();
		String tempStr;
		byte[] b = new byte[1024];
		int i = 0;
		while ((i = bis.read(b)) > 0) {
			tempStr = new String(b, 0, i, "GB2312");
			sBuffer.append(tempStr);
		}
		log.info("PostResult MerchantId：" + parameters.get("merchantId") + ",Code:" + connection.getResponseCode() + " ,Body："+ sBuffer.toString());
//		System.out.println("银生宝后台响应通知URL结果："+connection.getResponseCode());
		return String.valueOf(connection.getResponseCode());
	}

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
	public static String postUrl2(String url, Map parameters) throws IOException {
		// 商户不返回merchantKey
		parameters.remove("merchantKey");
		URLConnection connection = new URL(url).openConnection();
		// 设置超时时间
		connection.setConnectTimeout(30*1000);
		connection.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		if (parameters != null) {
			System.out.println(getParameterString(parameters));
			writer.write(getParameterString(parameters));
		}
		writer.flush();
		writer.close();

		// zln 修正 20080407 begin 修正读取反馈页面会出现sun.io.MalformedInputException的问题
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
		StringBuffer result = new StringBuffer();
		String tempStr = "";
		while((tempStr = br.readLine()) != null){
			result.append(tempStr);
		}
		return result.toString();
	}
	
	public static void postNetReturnUrl(final String url, final Map parameters)  {
		if(url == null || "".equals(url))
			return;
		
		new Thread(new Runnable() {
			public void run() {
				for(int i=1;i<=5;i++){
					try {
						String returnCode = HttpUtil.postUrlResult(url, parameters);
						if("200".equals(returnCode)){
							log.info("响应第三方通知成功！" + " phone：" + parameters.get("phone"));
//							System.out.println("银生宝后台响应商户通知成功！");
							break;
						}else{
							Thread.sleep(20000 + 10000*i);
						}
					} catch (Exception e) {
						log.info("响应第三方通知异常！" + " phone：" + parameters.get("phone"));
						e.printStackTrace();
						try {
							Thread.sleep(20000 + 10000*i);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		}).start();
	}

	/**
	 * 从指定输入流中读取一行字符串。一行以字符'\n'为末尾，文件末尾返回null
	 *
	 * @param in
	 *            输入流
	 * @return 字符串行
	 * @throws IOException
	 */
	public static String readLine(InputStream in) throws IOException {
		List list = new ArrayList();
		int b = -1;
		while ((b = in.read()) >= 0) {
			char c = (char) b;
			if (c != '\n') {
				list.add(new Character(c));
			} else {
				break;
			}
		}
		if (list.size() == 0) {
			return null;
		}
		byte[] bs = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			Character c = (Character) list.get(i);
			bs[i] = (byte) c.charValue();
		}
		return new String(bs);
	}
	
	
	static HttpConnectionManager connectionManager;
	private static HttpConnectionManager getConnectionManager() {
		if(connectionManager != null) return connectionManager;
		
		// 创建一个线程安全的HTTP连接池
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		// 连接建立超时
		params.setConnectionTimeout(30000);
		// 数据等待超时
		params.setSoTimeout(300000);
		// 默认每个Host最多10个连接
		params.setDefaultMaxConnectionsPerHost(10);
		// 最大连接数（所有Host加起来）
		params.setMaxTotalConnections(200);

		connectionManager.setParams(params);
		return connectionManager;
	}
	
}
