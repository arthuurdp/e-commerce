package com.arthuurdp.e_commerce.infrastructure.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.exceptions.MPException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void configureMercadoPago() throws MPException {
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}