package com.loan_manage.exception;

public class ArbitrationException extends RuntimeException {

    public ArbitrationException(){
        super();
    }

    public ArbitrationException(String message) {
        super(message);
    }

    public ArbitrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArbitrationException(Throwable cause) {
        super(cause);
    }
}
