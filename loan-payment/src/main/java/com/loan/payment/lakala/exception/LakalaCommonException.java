package com.loan.payment.lakala.exception;


public class LakalaCommonException extends RuntimeException {

    public LakalaCommonException(Throwable t) {
        super(t);
    }

    public LakalaCommonException(String errorMessage) {
        super(errorMessage);
    }

    public LakalaCommonException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
