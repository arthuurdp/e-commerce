package com.arthuurdp.e_commerce.modules.notification.dtos;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
}
