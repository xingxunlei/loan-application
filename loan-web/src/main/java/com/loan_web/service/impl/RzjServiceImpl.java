package com.loan_web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.loan_api.app.UserService;
import com.loan_entity.app.Person;
import com.loan_entity.rzj.RzjRegisterDo;
import com.loan_entity.rzj.RzjRegisterVo;
import com.loan_utils.util.CodeReturn;
import com.loan_utils.util.EmaySmsUtil;
import com.loan_utils.util.MD5Util;
import com.loan_web.common.constant.ErrorCode;
import com.loan_web.common.exception.CommonException;
import com.loan_web.common.util.ValidMac;
import com.loan_web.service.RzjService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;

/**
 * 融之家相关逻辑操作
 */
@Service
public class RzjServiceImpl implements RzjService {

    private final static Logger logger = LoggerFactory.getLogger(RzjServiceImpl.class);

    @Autowired
    private ValidMac validMac;

    @Autowired
    private UserService userService;

    @Value("${rzj.apply_url}")
    private String apply_url;
    @Value("${rzj.sms_massage}")
    private String massage;

    @Override
    public RzjRegisterDo rzjRegister(RzjRegisterVo vo) throws Exception{
        //定义返回数据
        RzjRegisterDo registerDo = new RzjRegisterDo();
        //json 解密
        String userAttribute = validMac.base64StrDecode(vo.getUser_attribute());
        logger.info("userAttribute 解密结果为"+userAttribute);
        if (userAttribute == null){
            throw new CommonException(ErrorCode.DECRYPTION_ERROR,String.format(ErrorCode.DECRYPTION_ERROR_VALUE,"user_attribute"));
        }
        //将融之家json数据转换
        Map<String,String> result = JSONObject.parseObject(userAttribute, Map.class);
        logger.info("userAttribute 转map结果为result = "+result);
        if (result == null){
            throw new CommonException( ErrorCode.PARAMETERS_ARE_MISSING,String.format(ErrorCode.PARAMETERS_ARE_MISSING_VALUE,"user_attribute"));
        }
        String phone = result.get("mobilephone");
        String channel_no = vo.getChannel_no();
        //判断json中是否有手机号
        if (phone == null){
            throw new CommonException(ErrorCode.PARAMETERS_ARE_MISSING,String.format(ErrorCode.PARAMETERS_ARE_MISSING_VALUE,"mobilephone"));
        }
        //随机生成六位数字
        int password = new Random().nextInt(999999);
        Person user = new Person();
        user.setPhone(phone);
        user.setPassword(MD5Util.encodeToMd5(String.valueOf(password)));
        user.setIsLogin(1);
        user.setSource(channel_no);
        String resp = userService.userRegister(user);
        logger.info("融之家注册结果为resp= "+resp);
        if (resp != null){
            Map<String,Object> registMap = JSONObject.parseObject(resp, Map.class);
            // 0代表成功
            registerDo.setCode("0");
            registerDo.setMsg("success");
            registerDo.setApply_url(apply_url);
            if (CodeReturn.PHONE_EXIST.equals(registMap.get("code"))){
                Object data = registMap.get("data");
                if (channel_no.equals(data)){
                    //2：通过借点钱渠道推送的老用户
                    registerDo.setIs_new_user("2");
                }else if (data != null){
                    //3：其他渠道推送的老用户
                    registerDo.setIs_new_user("3");
                }else {
                    throw new CommonException(ErrorCode.ERROR_CODE,ErrorCode.SYSTEM_ERROR);
                }
            }else if (200 ==(int) registMap.get("code")){
                registerDo.setIs_new_user("1");
                logger.info("用户注册成功需要发送短信的用户手机号为:"+phone);
                EmaySmsUtil.send(String.format(massage,phone,password),phone,1);
            }
        }else {
            throw new CommonException(ErrorCode.ERROR_CODE,ErrorCode.SYSTEM_ERROR);
        }
        return registerDo;
    }
}
