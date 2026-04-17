package com.notices.domain.errors;

public class WhatsAppSendException extends RuntimeException {

    public WhatsAppSendException(String message) {

        super(message);
    }

    public WhatsAppSendException(String message, Exception ex) {

        super(message, ex);
    }

}
