package com.loan_manage.utils;


public class RedisUtil {
	
	public final static String YM_ADMIN_SYSTEN_KEY = "ym_ds_admin_view";
	public final static String PERSON_KEY = "personInfo";
	public final static String BANK_KEY = "bankInfo";
	public final static String CARD_KEY = "cardInfo";
	public final static String PRODUCT_KEY = "productInfo";
	public final static String COUPON_KEY = "couponInfo";
    
    /**
     * 系统redis获取HASH数据
     * @param field
     * @param type
     * @return
     */
    public static String gethRedisData(String field, String type){
    	return gethRedisData(YM_ADMIN_SYSTEN_KEY, field, type);
    }
    /**
     * 通用redis获取HASH数据
     * @param key
     * @param field
     * @param type
     * @return
     */
    public static String gethRedisData(String key, String field, String type){
    	Assertion.notEmpty(key, "系统key不能为空");
    	Assertion.notEmpty(field, "fieldKey不能为空");
    	Assertion.notEmpty(type, "类型不能为空");
    	
    	String result =  JedisUtil.hget(key + type, field);
    	
    	return result;
    }
    
    /**
     * 系统redis设置HASH数据
     * @param field
     * @param type
     * @param data
     */
    public static void sethRedisData(String field, String type, String data){
    	sethRedisData(YM_ADMIN_SYSTEN_KEY, field, type, data);
    }
    /**
     * 通用redis设置HASH数据
     * @param key
     * @param field
     * @param type
     * @param data
     */
    public static void sethRedisData(String key, String field, String type, String data){
    	Assertion.notEmpty(key, "系统key不能为空");
    	Assertion.notEmpty(field, "fieldKey不能为空");
    	Assertion.notEmpty(type, "类型不能为空");
    	Assertion.notEmpty(data, "数据不能为空");
    	
    	JedisUtil.hset(key + type, field, data);
    }
    

}
