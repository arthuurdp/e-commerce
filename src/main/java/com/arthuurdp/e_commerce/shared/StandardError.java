package com.arthuurdp.e_commerce.shared;

import java.time.Instant;

public record StandardError(Instant timestamp, Integer status, String error, String message) {
}
