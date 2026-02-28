package com.arthuurdp.e_commerce.entities.dtos.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MercadoPagoWebhookRequest(
        String action,
        String type,

        @JsonProperty("data")
        WebhookData data
) {
    public record WebhookData(String id) {}
}