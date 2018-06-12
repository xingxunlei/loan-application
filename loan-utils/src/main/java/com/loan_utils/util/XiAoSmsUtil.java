package com.loan_utils.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class XiAoSmsUtil {
	
	private static final Logger logger = Logger.getLogger(XiAoSmsUtil.class);
	
	private static final String BASE_URL = "http://sms.10690221.com:9011/hy/";
	
	private static final String UID = "5069915";
	private static final String ENTERPRISE_CODE = "xacs";
	private static final String USER_PWD = "sioo15";

	public static boolean send(String message,String phone) {

		try {
			String auth = MD5Util.encodeToMd5(ENTERPRISE_CODE + USER_PWD);
			String content = URLEncoder.encode(message, "UTF-8");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", UID);
			map.put("auth", auth);
			map.put("mobile", phone);
			map.put("msg", content);
			map.put("expid", 0);
			map.put("encode", "UTF-8");
			String result = HttpUtils.sendGet(BASE_URL, HttpUtils.toParam(map));
			System.out.println("短信发送-phone:" + phone + ",结果:" + result);
			logger.error("短信发送-phone:" + phone + ",结果:" + result);
			if(Detect.notEmpty(result)){
				String[] restlts = result.split(",");
				if(Detect.notEmpty(restlts)){
					return Integer.valueOf(restlts[0]) == 0 ? true : false;
				}
			}
			return false;
		} catch (Exception e) {
			logger.error("短信发送异常 phone:" + phone + "------------:" + e.getMessage(), e);
			return false;
		}
	}
	
	public static void main(String[] args) {

//		String phone = "15618973047";
//		String[] phones = phone.split(",");
//		System.out.println();
//		String result = "【悠米闪借】尊敬的用户，您本次账单人民币1002.0。到期还 款日2017-07-30 。"
//				+ "http://uat.youmishanjie.com/static/register/youmiRepayment.html";
//		for(String p : phones){
//			System.out.println(WuXunSmsUtil.sendResult(result,  p, 1));
//		}
		String filePath = "C:\\Users\\wanzezhong\\Desktop\\phone.txt";
		readTxtFile(filePath);
//		send("【悠米闪借】尊敬的“万泽中”，由于您的综合评分不足，发放借款失败。","13636569813");
	}
	public static void readTxtFile(String filePath){
		try {
			String encoding="UTF-8";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ //判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while((lineTxt = bufferedReader.readLine()) != null){
//					System.out.println(lineTxt);
					String[] aa =  lineTxt.split(",");
//					System.out.println(aa[1] + aa[0]);
//					String msg = "【悠米闪借】尊敬的"+ aa[0]+ "，您申请的1000、2017-11-09借款已发放至您指定账户，请注意查收。请不要忘记在2017-11-16及时回款";
//					String msg = "【悠米闪借】尊敬的"+ aa[0]+ "，由于您的综合评分不足，发放借款失败。";
//					String msg = "【悠米闪借】尊敬的" + aa[0]+ "，您本次账单人民币1000。到期回款日2017-11-10。";
//					String msg = "【悠米闪借】尊敬的"+ aa[0]+ "，悠米闪借于2017-11-09已成功扣款1000，感谢您对悠米闪借平台的信任与支持，我们依然会竭诚为您服务！";
//					String msg = "【悠米闪借】尊敬的用户，您于 “2017-11-09”已成功回 款1000元，感谢您的支持！";
					String msg = "【悠米闪借】尊敬的会员，温馨提示：距离成功放 款还有一步之遥奥！立即点击http://t.cn/RlRCppTl领取！";

					System.out.println(XiAoSmsUtil.send(msg,  aa[1]));
				}
				read.close();
			}else{
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

	}


}
