package com.notices.domain.errors;

public class SmsSendException extends RuntimeException {

    public SmsSendException(String message) {

        super(message);
    }

    public SmsSendException(String message, Exception ex) {

        super(message, ex);
    }

}
