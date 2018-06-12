package com.loan.payment.service;

import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.auth.AuthRequest;
import com.loan_entity.lakala.auth.AuthResponse;

/**
 * 拉卡拉认证接口
 */
public interface LakalaAuthService {

    /**
     * 拉卡拉认证接口
     * @param authReq  认证请求参数
     * @param dataHead
     * @return
     * @throws LakalaClientException
     */
    AuthResponse auth(AuthRequest authReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;
}
