package com.loan.payment.service.impl;

import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan.payment.lakala.util.LakalaMsgUtil;
import com.loan.payment.service.LakalaAuthService;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPayEncryptResponse;
import com.loan_entity.lakala.auth.AuthRequest;
import com.loan_entity.lakala.auth.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LakalaAuthServiceImpl implements LakalaAuthService {

    @Autowired
    private LakalaCrossPayRestfulService payRestfulService;

    private static final Logger logger = LoggerFactory.getLogger(LakalaAuthServiceImpl.class);

    public AuthResponse auth(AuthRequest authReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        AuthResponse authRes = null;
        LakalaCrossPayEncryptRequest req = LakalaMsgUtil.encryptMsg(authReq, dataHead);
        try {
            LakalaCrossPayEncryptResponse encryptRes = payRestfulService.doPost(LakalaCrossPayEncryptResponse.class, req, "/gate/auth");

            if ("0000".equals(encryptRes.getRetCode())) {
                authRes = LakalaMsgUtil.decrypt(encryptRes, AuthResponse.class);
            } else {
                authRes = new AuthResponse();
            }
            authRes.setRetCode(encryptRes.getRetCode());
            authRes.setRetMsg(encryptRes.getRetMsg());
            authRes.setVer(encryptRes.getVer());
            authRes.setPayTypeId(encryptRes.getPayTypeId());
            authRes.setReqType(encryptRes.getReqType());
            authRes.setTs(encryptRes.getTs());
            authRes.setMerId(encryptRes.getMerId());
        } catch (Exception e) {
            logger.error("拉卡拉认证 error", e);
            throw new LakalaClientException(e.getMessage(), e);
        }
        return authRes;
    }
}
