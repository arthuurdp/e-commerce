package com.arthuurdp.e_commerce.entities.dtos.shipping;

import  com.arthuurdp.e_commerce.entities.enums.ShippingStatus;

public record UpdateShippingRequest(ShippingStatus status) {}