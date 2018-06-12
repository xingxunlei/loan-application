package com.loan.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.payment.mapper.CardMapper;
import com.loan.payment.mapper.CodeValueMapper;
import com.loan.payment.mapper.PersonMapper;
import com.loan.payment.service.RedisService;
import com.loan_entity.app.Card;
import com.loan_entity.app.Person;
import com.loan_entity.common.Constants;
import com.loan_entity.manager.CodeValue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

/**
 * Redis相关
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private CardMapper cardMapper;

    public String selectGatherFee() {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.PAY_FEE_TAG;

        String feeResult = jedisCluster.get(key);
        if(StringUtils.isNotEmpty(feeResult) && "nil".equals(feeResult)){
            return feeResult;
        }else{
            CodeValue queryCodeValue = new CodeValue();
            queryCodeValue.setCodeType("payment_fee");
            queryCodeValue.setCodeCode("1");

            CodeValue codeValue = codeValueMapper.selectOne(queryCodeValue);

            if(codeValue != null){
                String value = codeValue.getMeaning();
                jedisCluster.set(key,value);

                return value;
            }

            return "";
        }
    }

    public JSONObject selectPersonFromRedis(Integer customerId) {
        String key = Constants.YM_ADMIN_SYSTEN_KEY + Constants.PERSON_KEY;
        String personStr = jedisCluster.hget(key,String.valueOf(customerId));
        JSONObject resultObj = null;
        if(StringUtils.isBlank(personStr)){
            resultObj = new JSONObject();
            //查询用户电话
            Person person = personMapper.selectByPrimaryKey(customerId);//personService.selectPersonByPersonId(customerId);
            //查询用户信息
            Card queryCard = new Card();
            queryCard.setPerId(customerId);
            Card personCard = cardMapper.selectOne(queryCard);
            if(person != null){
                resultObj.put("phone",person.getPhone());
            }
            if(personCard != null){
                resultObj.put("name",personCard.getName());
                resultObj.put("idCard",personCard.getCardNum());
            }
            jedisCluster.hset(key,String.valueOf(customerId),resultObj.toJSONString());
            jedisCluster.expire(key,14*24*60*60);//14天
        }else{
            resultObj = JSON.parseObject(personStr);
        }
        return resultObj;
    }
}
