package com.arthuurdp.e_commerce.domain.dtos.shipping;

import com.arthuurdp.e_commerce.domain.enums.ShippingStatus;

public record UpdateShippingRequest(ShippingStatus status, String trackingCode) {}