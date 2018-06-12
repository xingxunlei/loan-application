package com.loan_web.service;

import com.loan_entity.rzj.RzjRegisterDo;
import com.loan_entity.rzj.RzjRegisterVo;

/**
 * 融之家相关逻辑操作
 */
public interface RzjService {

    /**
     *  融之家注册
     * @param vo 请求参数
     * @return  返回响应
     */
    public RzjRegisterDo rzjRegister(RzjRegisterVo vo) throws Exception;
}
