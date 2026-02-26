package com.arthuurdp.e_commerce.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message, Exception e) {
        super(message);
    }
}
