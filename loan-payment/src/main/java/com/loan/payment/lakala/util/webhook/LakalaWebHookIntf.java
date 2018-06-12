package com.loan.payment.lakala.util.webhook;


import com.loan_entity.lakala.LakalaCrossPayEncryptRequest;
import com.loan_entity.lakala.LakalaCrossPayEncryptResponse;

/**
 * 拉卡拉跨境支付webhook接口
 */
public interface LakalaWebHookIntf<T extends LakalaCrossPayEncryptResponse, E extends LakalaCrossPayEncryptRequest> {

    /**
     * 响应拉卡拉回调
     * @param notify 拉卡拉回调参数
     * @return
     */
    T proceed(E notify);
}
