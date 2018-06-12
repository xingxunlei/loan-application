package com.loan.payment.lakala.util.webhook;


import com.loan.payment.lakala.exception.LakalaCommonException;
import com.loan_entity.lakala.webHook.SuperWebHookRequest;

/**
 * 处理拉卡拉通知<em>商户需根据自己业务实现该接口</em>
 */
public interface WebHookHandler<T extends SuperWebHookRequest> {

    /**
     * 处理拉卡拉通知
     * @param notifyMsg
     * @return
     * @throws LakalaCommonException
     */
    void handle(T notifyMsg) throws LakalaCommonException;
}
