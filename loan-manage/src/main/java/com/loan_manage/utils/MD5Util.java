package com.loan_manage.utils;

import java.security.MessageDigest;

import com.loan_utils.util.PropertiesReaderUtil;
import org.apache.log4j.Logger;

public class MD5Util {

    private static Logger log = Logger.getLogger(MD5Util.class);

    /**
     * MD5
     * 签名
     *
     * @param paramter:请求参数字符串
     * @return
     * @author chennan
     */
    public final static String encryptString(String paramter) {
        String sign = "";
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = paramter.getBytes("UTF-8");
            // 使用MD5创建MessageDigest对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                // 将没个数(int)b进行双字节加密
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            sign = new String(str);
        } catch (Exception e) {
            log.error("32位小写md5错误,paramter:".concat(paramter), e);
        }
        return sign;
    }

    public static String encryptStringBySign(String word) {
        String mySign = PropertiesReaderUtil.read("sign", "sign");
        return MD5Util.encryptString(mySign + "." + word);
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.encryptString("123123"));
    }
}
