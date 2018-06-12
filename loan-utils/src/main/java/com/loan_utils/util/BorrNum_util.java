package com.loan_utils.util;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * 生成借款编号工具类，当前时间精确到毫秒+四位随机数
 * @author xuepengfei
 *2016年10月12日上午10:12:31
 */
public class BorrNum_util {

    public static String createBorrNum(){
        long time = System.currentTimeMillis();
        int rand = (int)(Math.random()*9000+1000);
        String num = String.valueOf(time)+String.valueOf(rand);
        return num;
    }
    
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();
        // 参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

//    public static String getIdByType(String type) {
//
//    }

    public static void main(String[] args) {
    	DecimalFormat df=new DecimalFormat("######0.00");
    	double d=1252.2;
    	String st=df.format(d);
    	System.out.println(st);	
	}
}
