package com.loan.payment.service.impl;

import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan.payment.lakala.util.LakalaMsgUtil;
import com.loan.payment.service.LakalaPayService;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPayEncryptResponse;
import com.loan_entity.lakala.gather.LakalaGatherRequest;
import com.loan_entity.lakala.gather.LakalaGatherResponse;
import com.loan_entity.lakala.otp.LakalaOtpRequest;
import com.loan_entity.lakala.otp.LakalaOtpResponse;
import com.loan_entity.lakala.payAgent.LakalaPayAgentRequest;
import com.loan_entity.lakala.payAgent.LakalaPayAgentResponse;
import com.loan_entity.lakala.payment.PaymentRequest;
import com.loan_entity.lakala.payment.PaymentResponse;
import com.loan_entity.lakala.recon.ReconSubscribeRequest;
import com.loan_entity.lakala.recon.ReconSubscribeResponse;
import com.loan_entity.lakala.refund.RefundRequest;
import com.loan_entity.lakala.refund.RefundResponse;
import com.loan_entity.lakala.submit.SubmitOrderRequest;
import com.loan_entity.lakala.submit.SubmitOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service @Scope("prototype")
public class LakalaPayServiceImpl implements LakalaPayService {

    private static final Logger logger = LoggerFactory.getLogger(LakalaPayServiceImpl.class);

    @Autowired
    private LakalaCrossPayRestfulService payRestfulService;

    public SubmitOrderResponse submitOrder(SubmitOrderRequest order, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        return null;
    }

    public PaymentResponse pay(PaymentRequest payOrder, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        return null;
    }

    public LakalaOtpResponse sendOtp(LakalaOtpRequest otpReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        return null;
    }

    public LakalaGatherResponse gather(LakalaGatherRequest gatherOrder, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        LakalaGatherResponse res = null;
        LakalaCrossPayEncryptRequest req = LakalaMsgUtil.encryptMsg(gatherOrder, dataHead);
        try {
            LakalaCrossPayEncryptResponse encryptRes = payRestfulService.doPost(LakalaCrossPayEncryptResponse.class, req, "/ppayGate/merCrossBorderAction.do");

            if ("0000".equals(encryptRes.getRetCode())) {
                res = LakalaMsgUtil.decrypt(encryptRes, LakalaGatherResponse.class);
            } else {
                res = new LakalaGatherResponse();
            }
            res.setRetCode(encryptRes.getRetCode());
            res.setRetMsg(encryptRes.getRetMsg());
            res.setVer(encryptRes.getVer());
            res.setPayTypeId(encryptRes.getPayTypeId());
            res.setReqType(encryptRes.getReqType());
            res.setTs(encryptRes.getTs());
            res.setMerId(encryptRes.getMerId());
        } catch (Exception e) {
            logger.error("拉卡拉跨境支付-单笔实时收款 error", e);
            throw new LakalaClientException(e.getMessage(), e);
        }
        return res;
    }

    public LakalaPayAgentResponse agentPay(LakalaPayAgentRequest agentPayReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        return null;
    }

    public RefundResponse refun(RefundRequest refundReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        return null;
    }

    public ReconSubscribeResponse reconSubscribe(ReconSubscribeRequest reconSubscribeReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException {
        return null;
    }
}
