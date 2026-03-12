package com.arthuurdp.e_commerce.modules.shipping;

import com.arthuurdp.e_commerce.modules.shipping.client.MelhorEnvioClient;
import com.arthuurdp.e_commerce.modules.shipping.dtos.FreightResponse;
import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.shared.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.cart.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class FreightService {
    private final MelhorEnvioClient client;
    private final CartRepository repo;

    public FreightService(MelhorEnvioClient client, CartRepository repo) {
        this.client = client;
        this.repo = repo;
    }

    @Transactional
    public List<FreightResponse> calculate(String postalCode, User user) {
        Cart cart = repo.findById(user.getCart().getId()).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        return client.calculate(
                postalCode, cart.quantity())
                .stream().filter(MelhorEnvioClient.FreightOption::isAvailable)
                .sorted(Comparator.comparing(MelhorEnvioClient.FreightOption::price))
                .map(option -> new FreightResponse(
                        option.id(),
                        option.name(),
                        option.price(),
                        option.deliveryDays()
                ))
                .toList();
    }
}
