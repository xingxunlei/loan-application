package com.loan_utils.payment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.loan_utils.util.Base64Utils;
import com.loan_utils.util.HttpUrlPost;
import com.loan_utils.util.MD5Util;
import com.loan_utils.util.PropertiesReaderUtil;
import com.loan_utils.util.RSAUtils;

/**
 * 
 * @author zhangweixing
 * @time 2016年11月9日 下午1:48:37
 */
public class PaymentUtil {

	private static final Logger logger = LoggerFactory.getLogger(PaymentUtil.class);
	/**
	 * 支付接口
	 * 
	 * @param orderId
	 *            订单号
	 * @param amount
	 *            金额
	 * @param responseUrl
	 *            前台响应地址
	 * @param backResponseUrl
	 *            后台响应地址
	 * @param customerId
	 *            用户id
	 * @param name
	 *            用户名
	 * @param cardNo
	 *            银行卡号
	 * @param certNo
	 *            身份证号
	 * @throws Exception
	 */
	public static String payment(String orderId, String amount,
			String responseUrl, String backResponseUrl, String customerId,
			String name, String cardNo, String certNo) throws Exception {
		String accountId = PropertiesReaderUtil.read("third", "accountId");
		String paymentUrl = PropertiesReaderUtil.read("third", "paymentUrl");
		String privateKey = PropertiesReaderUtil.read("third", "privateKey");
		String data = "";
		//测试为了通过暂时写成0.01
		String isTest = PropertiesReaderUtil.read("third", "isTest");
		if (isTest.equals("on")) {
			amount = "0.01";
		}
		
		StringBuffer custStr = new StringBuffer(customerId);
		custStr.append("|").append(name);
		custStr.append("|").append(cardNo);
		custStr.append("|").append(certNo);

		// 用户信息加密
		byte[] encodedData = RSAUtils.encryptByPrivateKey(custStr.toString()
				.getBytes(), privateKey);
		data = Base64Utils.encode(encodedData);
        // System.out.println(data);
		StringBuffer signStr = new StringBuffer(accountId);
		signStr.append("&").append(orderId);
		signStr.append("&").append(amount);
		// signStr.append("&").append(responseUrl);
		signStr.append("&").append(backResponseUrl);
		signStr.append("&").append(data);
		// 数字签名
		String sign = RSAUtils.sign(signStr.toString().getBytes(), privateKey);
        // System.out.println(sign);

		// 特殊符号处理
		// String data2 = data.replace("+", "%2B");
		// data2 = data2.replace("/","%2F");
		// data2 = data2.replace("=","%3D");
		// sign = sign.replace("+", "%2B");
		// sign = sign.replace("/","%2F");
		// sign = sign.replace("=","%3D");
		// 请求银生宝的支付接口
		JSONObject obj = new JSONObject();
		obj.put("url", paymentUrl);
		obj.put("accountId", accountId);
		obj.put("orderId", orderId);
		obj.put("amount", amount);
		obj.put("responseUrl", responseUrl);
		obj.put("backResponseUrl", backResponseUrl);
		obj.put("data", data);
		obj.put("sign", sign);
		return obj.toString();
		// HttpUrlPost.sendPost(paymentUrl, paramMap);
	}
	
	
	

	/**
	 * 代付接口
	 * 
	 * @param name
	 *            用户姓名
	 * @param cardNo
	 *            银行卡号
	 * @param orderId
	 *            订单号
	 * @param purpose
	 *            目的
	 * @param amount
	 *            金额
	 * @param responseUrl
	 *            返回的url地址
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String payCont(String name, String cardNo, String orderId,
			String purpose, String amount, String responseUrl)
			throws UnsupportedEncodingException {
//		name="曹华强";
//		cardNo="6217001210080236795";
		String isTest = PropertiesReaderUtil.read("third", "isTest");
		if (isTest.equals("on")) {
			amount = "0.01";
		}
		// 获取商户号
		String accountId = PropertiesReaderUtil.read("third", "accountId");
		// 获取加密用的key
		String key = PropertiesReaderUtil.read("third", "key");
		// 获取代付的地址
		String daifuUrl = PropertiesReaderUtil.read("third", "daifuUrl");

		// 进行加密得到mac签名
		StringBuffer macStr = new StringBuffer("accountId=" + accountId);
		macStr.append("&").append("name=" + name);
		macStr.append("&").append("cardNo=" + cardNo);
		macStr.append("&").append("orderId=" + orderId);
		macStr.append("&").append("purpose=" + purpose);
		macStr.append("&").append("amount=" + amount);
		macStr.append("&").append("responseUrl=" + responseUrl);
		macStr.append("&").append("key=" + key);
        // System.out.println(macStr.toString());
		String mac = MD5Util.encodeToMd5(macStr.toString()).toUpperCase();
		// 请求第三方接口
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("accountId", accountId);
		paramMap.put("name", name);
		paramMap.put("cardNo", cardNo);
		paramMap.put("orderId", orderId);
		paramMap.put("purpose", purpose);
		paramMap.put("amount", amount);
		paramMap.put("responseUrl", responseUrl);
		paramMap.put("mac", mac);
		System.out.println("请求第三方支付参数====="+"orderId:"+ orderId+",amount:"+ amount+",cardNo:"+ cardNo+",name:"+ name+",requestUrl："+daifuUrl+",responseUrl："+ responseUrl);
		String result = HttpUrlPost.sendPost(daifuUrl, paramMap);
        // System.out.println("放款代付接口返回==" + result);
		System.out.println("第三方支付响应=====orderId:"+orderId+",result:"+result);
		return result;
	}

	/**
	 * 查询订单状态接口
	 * @param orderId
	 * @return
	 */
	public static String queryOrderStatus(String orderId) {
		String result = "";
//		orderId = "0116120116533373769";
		// 获取商户号
		String accountId = PropertiesReaderUtil.read("third", "accountId");
		// 获取加密用的key
		String key = PropertiesReaderUtil.read("third", "key");
		// 获取加密用的key
		String queryOrderStatusUrl = PropertiesReaderUtil.read("third", "queryOrderStatusUrl");
		// 进行加密得到mac签名
		StringBuffer macStr = new StringBuffer("accountId=" + accountId);
		macStr.append("&").append("orderId=" + orderId);
		macStr.append("&").append("key=" + key);
		String mac = MD5Util.encodeToMd5(macStr.toString()).toUpperCase();
		// 请求第三方接口
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("accountId", accountId);
		paramMap.put("orderId", orderId);
		paramMap.put("mac", mac);
		System.out.println("查询第三方认证支付参数====="+"orderId:"+ orderId+",requestUrl："+queryOrderStatusUrl);

		result = HttpUrlPost.sendPost(queryOrderStatusUrl, paramMap);
		System.out.println("查询第三方认证支付响应====="+"orderId:"+orderId+",result:" + result);
		// System.out.println("放款代付接口返回==" + result);
		return result;
	}
	
	
	/**
     * 查询还款订单状态接口
     * @param orderId
     * @return
     */
    public static String queryPayOrderStatus(String orderId) {
        String result = "";
//      orderId = "0116120116533373769";
        // 获取商户号
        String accountId = PropertiesReaderUtil.read("third", "accountId");
        // 获取加密用的key
        String key = PropertiesReaderUtil.read("third", "key");
        // 获取加密用的key
        String queryPayOrderStatusUrl = PropertiesReaderUtil.read("third", "queryPayOrderStatusUrl");
        // 进行加密得到mac签名
        StringBuffer macStr = new StringBuffer("accountId=" + accountId);
        macStr.append("&").append("orderId=" + orderId);
        macStr.append("&").append("key=" + key);
        String mac = MD5Util.encodeToMd5(macStr.toString()).toUpperCase();
        // 请求第三方接口
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("accountId", accountId);
        paramMap.put("orderId", orderId);
        paramMap.put("mac", mac);

		System.out.println("查询主动还款第三方参数====="+"orderId:"+ orderId+",requestUrl："+queryPayOrderStatusUrl);

		result = HttpUrlPost.sendPost(queryPayOrderStatusUrl, paramMap);
		System.out.println("查询主动还款第三方响应====="+"orderId:"+ orderId+",result:" + result);
		// System.out.println("认证支付接口返回==" + result);
        return result;
    }
	
	public static void main(String[] args) {
		String name = "曹华强";
		String cardNo = "6214851210774415";
		String certNo = "310109199301120011";
//		String accountId = PropertiesReaderUtil.read("third", "accountId");
//		String paymentUrl = PropertiesReaderUtil.read("third", "paymentUrl");
//		String privateKey = PropertiesReaderUtil.read("third", "privateKey");
		String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALAp4nEyBip7djuGhOx3SR2HVY5eLBhUnEquGq8DLc00u3XM2cjwSLek8a+XblBE3mDL0bEvzqxG8q15m0BbtexB7YV+YSRPfIONcsZjjI4SRJyxiIUZXVIO87f/xFz6NwW/EI88CEDsFKJwrNLwX40fEr6lcZvTe7BT1nFJTM0xAgMBAAECgYAvKpe5bQZfm3zVhTfTxEBsSo7qtmYiAY1zJVh5oWVC+ypx6O4qscMWgHBBbk8X6MiNrCSxxzYTxE83iUa7HZMAg+TO16thX8vsh15vB7pj3dc9HHod+cV4ySuXIXStet220OoPJhJLLtbBfKeiXhUEgoHqTMTHn5h4QQHTChpk/QJBAOCXSBfDqKz0LjuceGAGN0JY9MwEpH0iX0Fq+2U3lFYCEhjMlfDOPW3rv6nIrFmO8BoBCUPX4GHwclRG0y21XuMCQQDIzNOEyGPDmjNWMyzKl0EOWNQ1avs1VuEGDEUwW0hsBTDUBQjFczcFRd7F/97Ixyaayzrg9veeiUZZ3kD0oavbAkEApyYQIpecxrOoYPcv449cuwVHkzt1TCgzqpU7anY18NjzrP8+LIzhzRgiefL+ylyGN2REB/j3ZwyzmiNn8eHFLwJBAMU4sJPdgmdPIBcNInRFRUeDJcLltaB5GYEoMhjBv4shOgk0fJ36gNL0Ak909es794XYEJd+2kzjRa7k5eWgmTsCQQC5sRV+a8OVHeNUmyuEVf4bDcQQCxDOTc3J1RfoAY7mNY12xOePb2fF9XlHooIXauGagn5QqbqF6o6Z3JkrtlYt"; 
		StringBuffer custStr = new StringBuffer("6000221");
		custStr.append("|").append(name);
		custStr.append("|").append(cardNo);
		custStr.append("|").append(certNo);
		System.out.println(custStr.toString().getBytes());
		// 用户信息加密
		byte[] encodedData;
		try {
			encodedData = RSAUtils.encryptByPrivateKey(custStr.toString()
					.getBytes(), privateKey);
			String data = Base64Utils.encode(encodedData);
			System.out.println(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
