package com.loan.payment.service;

import com.loan.payment.lakala.exception.LakalaClientException;
import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPaySuperRequest;
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

/**
 * 拉卡拉支付接口
 */
public interface LakalaPayService {

    /**
     * 商户交订单
     * @param order    商户订单
     * @param dataHead 文头
     * @return SubmitOrderRes
     * @throws LakalaClientException
     */
    SubmitOrderResponse submitOrder(SubmitOrderRequest order, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;

    /**
     * 确认支付
     * @param payOrder 业务参数
     * @param dataHead 文头
     * @return PaymentRes
     * @throws LakalaClientException
     */
    PaymentResponse pay(PaymentRequest payOrder, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;


    /**
     * 卡 跨境支付-支付短信 证码重新获取
     * @param otpReq
     * @param dataHead
     * @return
     * @throws LakalaClientException
     */
    LakalaOtpResponse sendOtp(LakalaOtpRequest otpReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;


    /**
     * 卡跨境支付-单笔实时收款
     * @param gatherOrder
     * @param dataHead
     * @return
     * @throws LakalaClientException
     */
    LakalaGatherResponse gather(LakalaGatherRequest gatherOrder, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;

    /**
     * 卡 跨境支付-单笔实时代付
     * @param agentPayReq
     * @param dataHead
     * @return
     * @throws LakalaClientException
     */
    LakalaPayAgentResponse agentPay(LakalaPayAgentRequest agentPayReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;

    /**
     * 卡 跨境支付-退款 口
     * @param refundReq
     * @param dataHead
     * @return
     * @throws LakalaClientException
     */
    RefundResponse refun(RefundRequest refundReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;

    /**
     * 拉卡拉跨境支付-对账文件预约下载
     * @param reconSubscribeReq
     * @param dataHead
     * @return
     * @throws LakalaClientException
     */
    ReconSubscribeResponse reconSubscribe(ReconSubscribeRequest reconSubscribeReq, LakalaCrossPayEncryptRequest dataHead) throws LakalaClientException;
}
