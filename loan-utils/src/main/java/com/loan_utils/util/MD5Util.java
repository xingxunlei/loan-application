package com.loan_utils.util;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class MD5Util {
	public static String encodeToMd5(String source) {
		if(source==null)
			return null;
		
		try{
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			
			char hexDigits[] = {
					'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
					'e', 'f'};
			
			byte[] strTemp=source.getBytes("utf-8");
			mdTemp.update(strTemp);
			
			byte[] md = mdTemp.digest();
			int k = 0;
			int j = md.length;char str[] = new char[j * 2];
			
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>>4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args){
		String accountId = "1120161026094353001";
		String orderId = "0116120117012025184";
		String key = "123456abc";
		String queryOrderStatusUrl="http://180.166.114.155:8081/unspay-external/delegatePay/queryOrderStatus";
		StringBuffer macStr = new StringBuffer("accountId=" + accountId);
		macStr.append("&").append("orderId=" + orderId);
		macStr.append("&").append("key=" + key);
		String mac = MD5Util.encodeToMd5(macStr.toString()).toUpperCase();
		System.out.println(mac);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("accountId", accountId);
		paramMap.put("orderId", orderId);
		paramMap.put("mac", mac);
		String result = HttpUrlPost.sendPost(queryOrderStatusUrl, paramMap);
		System.out.println("放款代付接口返回==" + result);
	}
}
