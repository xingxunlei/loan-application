package com.loan.payment.lakala.util;

import java.util.Random;

/**
 * 工具类
 */
public class ToolsUtil {

    private static final ThreadLocal<String> LOCAL_MERKEY = new ThreadLocal<String>();

    /**
     * 随机生成字符串(自定义长度)
     * @param length 自定义长度
     * @return
     */
    public static String getRandomString(int length) {
        String radStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer generateRandStr = new StringBuffer();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int randNum = rand.nextInt(36);
            generateRandStr.append(radStr.substring(randNum, randNum + 1));
        }
        return generateRandStr + "";
    }

    /**
     * 获取当前线程的商户对称密钥
     * @return
     */
    public static String getMerKey() {
        if (null == LOCAL_MERKEY.get()) {
            String merKey = getRandomString(32);
            LOCAL_MERKEY.set(merKey);

            return merKey;
        } else {
            return LOCAL_MERKEY.get();
        }
    }

    /**
     * 设置对称密钥
     * @param key
     */
    public static void setMerKey(String key) {
        LOCAL_MERKEY.set(key);
    }

    public static void remove() {
        LOCAL_MERKEY.remove();
    }


}

