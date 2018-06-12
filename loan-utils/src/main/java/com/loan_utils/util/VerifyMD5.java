package com.loan_utils.util;
import com.loan_utils.util.PropertiesReaderUtil;
import java.util.Arrays;

public class VerifyMD5 {
    
    
    private static String mySign = "jhhymsj";
    
    public static boolean Sign(String nativeSign,String ...sign){
        sign = sort(sign);
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i < sign.length;i++){
            sb.append(sign[i]);
        }
        String mySign = PropertiesReaderUtil.read("sign", "sign");
        sb.append(mySign);
        return MD5Util.encodeToMd5(sb.toString()).equals(nativeSign);
    }
    public static String[] sort(String ...sign){
        Arrays.sort(sign);
        return sign;
    }
    public static String creatSign(String ...sign){
        sign = sort(sign);
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i < sign.length;i++){
            sb.append(sign[i]);
        }
        sb.append(mySign);
        System.out.println(sb.toString());
        return MD5Util.encodeToMd5(sb.toString());
    }
    public static void main(String[] args) {
        String naticSign = creatSign("6154511");
        System.out.println(naticSign);

    }
}
