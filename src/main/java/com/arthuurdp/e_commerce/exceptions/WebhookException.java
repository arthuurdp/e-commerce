package com.arthuurdp.e_commerce.exceptions;

public class WebhookException extends RuntimeException {
    public WebhookException(String message) {
        super(message);
    }
}
