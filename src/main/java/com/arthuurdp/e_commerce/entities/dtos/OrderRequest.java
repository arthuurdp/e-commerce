package com.arthuurdp.e_commerce.entities.dtos;

import java.util.List;

public record OrderRequest(
        Long addressId,
        List<OrderItemRequest> items
) {
}
