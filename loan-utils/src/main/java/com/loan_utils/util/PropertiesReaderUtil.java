package com.loan_utils.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件类
 *
 * @author zhangweixing
 * @time 2016年8月24日 下午5:47:03
 */
public class PropertiesReaderUtil {

    /**
     * 读取配置
     *
     * @param file 文件名,不带 .properties的后缀
     * @param name key名
     * @return
     */
    public static String read(String file, String name) {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = PropertiesReaderUtil.class.getResourceAsStream("/" + file + ".properties");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String udata = props.get(name).toString();
        return udata;

    }

    public static void main(String[] args) {
        String temp = PropertiesReaderUtil.read("sms", "account");
        System.out.println(temp);
    }

}
