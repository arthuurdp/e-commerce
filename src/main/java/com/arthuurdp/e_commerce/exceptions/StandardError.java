package com.arthuurdp.e_commerce.exceptions;

import java.time.Instant;

public record StandardError(Instant timestamp, Integer status, String error, String message) {
}
