package com.loan_utils.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @ClassName: EmaySmsUtil 
 * @Description: 亿美软通短信
 * @author: tangbo
 * @date: 2016年10月28日 下午3:08:51
 */
public class EmaySmsUtil {
	
	private static final Logger logger = Logger.getLogger(EmaySmsUtil.class);
	
    // private static final String SN = PropertiesReaderUtil.read("emay", "sn");
    private static final String SN = "9SDK-EMY-0999-RJUON";
	private static final String SN2 = "8SDK-EMY-6699-SFQTP";

	
    // private static final String baseUrl = PropertiesReaderUtil.read("emay",
    // "baseUrl");// sdk9请求地址
    private static final String baseUrl = "http://sh999.eucp.b2m.cn:8080/sdkproxy/";
	private static final String baseUrl2 = "http://hprpt2.eucp.b2m.cn:8080/sdkproxy/";

    // private static final String sn = getConfig("sn");//软件序列号
    // private static final String key = PropertiesReaderUtil.read("emay",
    // "key");// 序列号
    private static final String key = "558722";
	private static final String key2 = "020075";


    // private static final String sendMethod =
    // PropertiesReaderUtil.read("emay", "sendMethod");
    private static final String sendMethod = "post";
    // 发送请求方式get post


	/**
	 * 
	 * @Title: send 
	 * @Description: 短信发送
	 * @param phone
	 * @param type 0悠兔理财,1悠米闪借,2金狐贷
	 * @return: void
	 */
	public static boolean send(String message,String phone,int type){

		try {

			String mdn = phone;
			message = URLEncoder.encode(message, "UTF-8");
			String code = "";
			String ret ="";
			long seqId = System.currentTimeMillis();
			String param;
			String url;
			if (type == 1){
				param = "cdkey=" + SN + "&password=" + key + "&phone=" + mdn + "&message=" + message + "&addserial="
						+ code + "&seqid=" + seqId;
				url = baseUrl + "sendsms.action";
			}else{

				param = "cdkey=" + SN2 + "&password=" + key2 + "&phone=" + mdn + "&message=" + message + "&addserial="
						+ code + "&seqid=" + seqId;
				url = baseUrl2 + "sendsms.action";


			}
			System.out.println("param:"+param+",url:"+url);
			if ("get".equalsIgnoreCase(sendMethod)) {
				ret = SDKHttpClient.sendSMS(url, param);
			} else {
				ret = SDKHttpClient.sendSMSByPost(url, param);
			}
			logger.info("短信发送-phone:"+phone+",结果:" + ret);
			return "0".equals(ret) ? true : false;
		} catch (UnsupportedEncodingException e) {
			logger.error("短信发送异常 phone:"+phone,e);
			return false;
		}
	}
	
	public static void main(String[] args) {
        // boolean flag = EmaySmsUtil.send(
		// "【悠米闪借】尊敬的用户，由于系统原因，悠米闪借于2017-06-20给您多放款了855元借款。故于2017-07-07从您的银行卡上扣回了855元。给您带来的不便敬请谅解。如有疑问请咨询400-150-8990。谢谢。
		// 请",
		// "13341814726", 1);

//		 boolean flag = EmaySmsUtil.send("【金狐贷】万泽中你好，快来申请悠米闪借。", "13341814726", 2);
//		boolean flag = EmaySmsUtil.send("", "13341814726", 1);

//		System.out.println(flag);

//		String message1 = "【悠米闪借】尊敬的用户，由于系统原因，悠米闪借于2017-06-19给您多放款了855元借款。故于2017-08-10从您的银行卡上扣回了855元。给您带来的不便敬请谅解。如有疑问请咨询400-150-8990。谢谢。";
//		String message2 = "【悠米闪借】尊敬的用户，悠米闪借已向您尾号XXXX的银行卡退款XXXX元，请注意查收。如有问题请拨打客服电话400-150-8990。";



		 String list = readString3();
		 String[] lists = list.split("\n");
		 System.out.println(lists.length);
		 for (String s : lists) {
			 String phone = s.trim();
			 System.out.println(phone);

//			 String number = s.substring(0,11);
////			 System.out.println(number);
//			 String bank = s.substring(12,31);
//			 bank = bank.substring(15,19);
////			 System.out.println(bank);
//			 String money = s.substring(32,s.length());
//			 System.out.println(money);


			 String message = "【悠米闪借】尊敬的用户，请重新打开APP进行更新，更新后请重新进行认证，谢谢您的配合。";
			 System.out.println(message);
			  boolean flag = EmaySmsUtil.send(message,phone,1);
			 System.out.println(flag);

		 }


	}
	
    private static String readString3()

    {

        String str = "";

        File file = new File("D:\\1030.txt");

        try {

            FileInputStream in = new FileInputStream(file);

            // size 为字串的长度 ，这里一次性读完

            int size = in.available();

            byte[] buffer = new byte[size];

            in.read(buffer);

            in.close();

            str = new String(buffer, "UTF8");

        } catch (IOException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();
            return "";

        }

        return str;

    }

}
