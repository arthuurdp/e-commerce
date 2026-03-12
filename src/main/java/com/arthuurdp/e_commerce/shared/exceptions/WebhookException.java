package com.arthuurdp.e_commerce.shared.exceptions;

public class WebhookException extends RuntimeException {
    public WebhookException(String message) {
        super(message);
    }
}
