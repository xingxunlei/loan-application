package com.loan.payment.lakala.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期类 具
 */
public class DateUtil {
    private static String ymdhms = "yyyyMMddHHmmssssssss";

    /**
     * 获得当前时间
     * 格式：yyyyMMddHHmmssssssss
     *
     * @return String
     */
    public static String getCurrentTime() {
        SimpleDateFormat yyyyMMddHHmmssssssss = new SimpleDateFormat(ymdhms);
        return yyyyMMddHHmmssssssss.format(new Date());
    }

    /**
     * 返回一个格式化的日期字符串
     *
     * @param pattern 日期格式，若为空则默认为yyyyMMdd
     * @return
     */
    public static String getCurrentDate(String pattern) {
        SimpleDateFormat datePattern = null;
        if (null == pattern || "".equals(pattern)) {
            datePattern = new SimpleDateFormat("yyyyMMdd");
        } else {
            datePattern = new SimpleDateFormat(pattern);
        }
        return datePattern.format(new Date());
    }
}
