package com.loan.payment.lakala.exception;

/**
 * 拉卡拉跨支付client异常,向拉卡拉跨境支付平台发起请求时可能会抛出该异常
 */
public class LakalaClientException extends RuntimeException {

    public LakalaClientException(Throwable t) {
        super(t);
    }

    public LakalaClientException(String errorMessage) {
        super(errorMessage);
    }

    public LakalaClientException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
