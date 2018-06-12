package com.loan_utils.util;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 
 * <p> Description: http/https报文发送处理类 </p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Create Date: 2014-01-23 </p>
 * <p> Company: CITIC BANK </p> 
 * @author CITIC
 * @version $Id: HttpUtil.java,v 1.0.0
 */
public class HttpsUtil {

	public static String encoding = "GBK";

    private static final HttpConnectionManager connectionManager;
    
    private static final HttpClient client;

    static {

    	HttpConnectionManagerParams params = loadHttpConfFromFile();
    	
    	connectionManager = new MultiThreadedHttpConnectionManager();

        connectionManager.setParams(params);

        client = new HttpClient(connectionManager);
    }
    
    private static HttpConnectionManagerParams loadHttpConfFromFile(){
		
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(15000);
        params.setSoTimeout(30000);
        params.setStaleCheckingEnabled(Boolean.parseBoolean("true"));
        params.setTcpNoDelay(Boolean.parseBoolean("true"));
        params.setDefaultMaxConnectionsPerHost(100);
        params.setMaxTotalConnections(1000);
        params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
		return params;
    }

    

	public static String sendPost(String url, Map<String,String> paras) {
    	return post(url, encoding, paras);
    }

	public static String post(String url, String encoding, Map<String, String> paras) {
		try {
			byte[] resp = post(url, paras);
			if (null == resp)
				return null;
			return new String(resp, encoding);
		} catch (Exception e) {
			return null;
		}
	}
	

    public static byte[] post(String url, Map<String, String> params) throws Exception {
    	
        Protocol myhttps = new Protocol("https", new HttpsProSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        
    	PostMethod method = new PostMethod(url);
        for (String key : params.keySet()) {
            NameValuePair param = new NameValuePair();
            param.setName(key);
            param.setValue(params.get(key).trim());
            method.addParameter(param);
        }
        method.setContentChunked(false);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            return method.getResponseBody();

        } catch (SocketTimeoutException e) {
        	return null;
        } catch (Exception e) {
        	return null;
        } finally {
            method.releaseConnection();
        }
    }
    
    
}
