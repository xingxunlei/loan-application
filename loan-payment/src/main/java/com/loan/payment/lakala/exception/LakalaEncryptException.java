package com.loan.payment.lakala.exception;

/**
 * 加密异常
 */
public class LakalaEncryptException extends RuntimeException {
    public LakalaEncryptException(Throwable t) {
        super(t);
    }

    public LakalaEncryptException(String errorMessage) {
        super(errorMessage);
    }

    public LakalaEncryptException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
