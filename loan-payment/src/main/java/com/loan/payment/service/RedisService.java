package com.loan.payment.service;

import com.alibaba.fastjson.JSONObject;

public interface RedisService {

    /**
     * 获取代扣手续费
     * @return
     */
    String selectGatherFee();

    /**
     * 根据用户ID从redis中取出用户
     * @param customerId 用户ID
     * @return {name:'张三',idCard:'1234567890',phone:'1365689745'}
     */
    JSONObject selectPersonFromRedis(Integer customerId);
}
