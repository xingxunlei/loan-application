package com.loan_utils.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 生成订单的流水号  传参为类型（2位数字）
 * @author xuepengfei
 *2016年11月14日下午3:41:33
 */
public class SerialNumUtil {
    public static String createByType(String type){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
        String now = sdf.format(new Date());
        Random random = new Random();        
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数 
        return type+now+rannum;
    }

}
