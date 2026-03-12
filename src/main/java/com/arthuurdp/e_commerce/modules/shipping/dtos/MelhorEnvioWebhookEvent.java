package com.arthuurdp.e_commerce.modules.shipping.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MelhorEnvioWebhookEvent(
        @JsonProperty("order_id")
        String orderId,

        String status,
        String description,

        @JsonProperty("created_at")
        String createdAt
) {}
