package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.clients.MelhorEnvioClient;
import com.arthuurdp.e_commerce.clients.MelhorEnvioClient.AddToCartRequest;
import com.arthuurdp.e_commerce.clients.MelhorEnvioClient.FreightOption;
import com.arthuurdp.e_commerce.clients.MelhorEnvioClient.MelhorEnvioApiException;
import com.arthuurdp.e_commerce.domain.dtos.shipping.MelhorEnvioWebhookEvent;
import com.arthuurdp.e_commerce.domain.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.domain.entities.Address;
import com.arthuurdp.e_commerce.domain.entities.Order;
import com.arthuurdp.e_commerce.domain.entities.Shipping;
import com.arthuurdp.e_commerce.domain.enums.ShippingStatus;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.ShippingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ShippingService {
    private static final Logger log = LoggerFactory.getLogger(ShippingService.class);

    private static final String TRACKING_URL = "https://melhorrastreio.com.br/rastreio/";

    private final ShippingRepository shippingRepository;
    private final MelhorEnvioClient melhorEnvioClient;
    private final EntityMapperService mapper;

    @Value("${melhorenvio.from-postal-code}")
    private String fromPostalCode;

    @Value("${melhorenvio.store-name}")
    private String storeName;

    @Value("${melhorenvio.store-document}")
    private String storeDocument;

    @Value("${melhorenvio.store-email}")
    private String storeEmail;

    @Value("${melhorenvio.store-phone}")
    private String storePhone;

    @Value("${melhorenvio.store-address}")
    private String storeAddress;

    @Value("${melhorenvio.store-number}")
    private String storeNumber;

    @Value("${melhorenvio.store-district}")
    private String storeDistrict;

    @Value("${melhorenvio.store-city}")
    private String storeCity;

    @Value("${melhorenvio.store-state}")
    private String storeState;

    public ShippingService(ShippingRepository shippingRepository, MelhorEnvioClient melhorEnvioClient, EntityMapperService mapper) {
        this.shippingRepository = shippingRepository;
        this.melhorEnvioClient  = melhorEnvioClient;
        this.mapper = mapper;
    }

    @Transactional
    public void createForOrder(Order order) {
        Shipping shipping = shippingRepository.findByOrderId(order.getId()).orElseGet(() -> shippingRepository.save(new Shipping(order)));

        if (shipping.getStatus() == ShippingStatus.LABEL_GENERATED) {
            log.info("Order {}: shipping label already generated — skipping", order.getId());
            return;
        }

        try {
            String toPostalCode = order.getAddress().getPostalCode().replaceAll("\\D", "");

            if (shipping.getMeOrderId() == null) {
                int itemCount = order.getItems().size();
                List<FreightOption> options = melhorEnvioClient.calculate(toPostalCode, itemCount);
                FreightOption chosen = options.stream()
                        .filter(FreightOption::isAvailable)
                        .min(Comparator.comparing(FreightOption::price))
                        .orElseThrow(() -> new MelhorEnvioApiException("No freight options available for CEP " + toPostalCode));

                log.info("Order {}: chose freight '{}' at R$ {} ({} days)",
                        order.getId(), chosen.name(), chosen.price(), chosen.deliveryDays());

                var cartRequest = buildCartRequest(order, chosen, toPostalCode);
                String meOrderId = melhorEnvioClient.addToCart(cartRequest);
                shipping.setMeOrderId(meOrderId);
                shipping.setCarrier(chosen.name());
                shipping.setShippingCost(chosen.price());
                shippingRepository.save(shipping);
            }

            if (shipping.getStatus() == ShippingStatus.PENDING || shipping.getStatus() == ShippingStatus.FAILED) {
                melhorEnvioClient.purchase(List.of(shipping.getMeOrderId()));
                shipping.setStatus(ShippingStatus.PURCHASED);
                shippingRepository.save(shipping);
            }

            if (shipping.getStatus() == ShippingStatus.PURCHASED) {
                var labelInfo = melhorEnvioClient.generateLabel(shipping.getMeOrderId());
                shipping.setTrackingCode(labelInfo.trackingCode());
                shipping.setTrackingUrl(TRACKING_URL + labelInfo.trackingCode());
                shipping.setLabelUrl(labelInfo.labelUrl());
                shipping.setStatus(ShippingStatus.LABEL_GENERATED);
                shippingRepository.save(shipping);
                log.info("Order {}: label generated — tracking {}", order.getId(), labelInfo.trackingCode());
            }

        } catch (Exception e) {
            shipping.setStatus(ShippingStatus.FAILED);
            shippingRepository.save(shipping);
            log.error("Order {}: ME label creation failed — {}", order.getId(), e.getMessage(), e);
        }
    }

    @Transactional
    public void handleWebhookEvent(MelhorEnvioWebhookEvent event) {
        Shipping shipping = shippingRepository.findByMeOrderId(event.orderId()).orElseThrow(() -> new ResourceNotFoundException("No shipping found for ME order: " + event.orderId()));

        ShippingStatus newStatus = mapMeStatus(event.status());
        if (newStatus == null) {
            log.debug("Ignoring unknown ME status '{}' for order {}", event.status(), event.orderId());
            return;
        }

        shipping.setStatus(newStatus);

        if (newStatus == ShippingStatus.POSTED) {
            shipping.setPostedAt(LocalDateTime.now());
        } else if (newStatus == ShippingStatus.DELIVERED) {
            shipping.setDeliveredAt(LocalDateTime.now());
        }

        shippingRepository.save(shipping);
        log.info("Shipping for ME order {} updated to {}", event.orderId(), newStatus);
    }

    public ShippingResponse getShippingForUser(Long orderId, Long userId) {
        Shipping shipping = shippingRepository.findByOrderIdAndOrderUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shipping not found for order " + orderId));
        return mapper.toShippingResponse(shipping);
    }

    private AddToCartRequest buildCartRequest(Order order, FreightOption option, String toPostalCode) {
        Address addr = order.getAddress();
        String cityName = addr.getCity().getName();
        String stateUf = addr.getCity().getState().getUf();
        String numStr = String.valueOf(addr.getNumber());
        String name = order.getUser().getFirstName() + " " + order.getUser().getLastName();
        String email = order.getUser().getEmail();
        String doc = order.getUser().getCpf();
        String phone = order.getUser().getPhone();

        var from = new AddToCartRequest.FromAddress(
                storeName, storeEmail, storeDocument, storePhone,
                fromPostalCode,
                storeAddress, storeNumber,
                storeDistrict, storeCity, storeState
        );

        var to = new AddToCartRequest.ToAddress(
                name, email, doc, phone,
                toPostalCode,
                addr.getStreet(), numStr,
                addr.getNeighborhood(), cityName, stateUf
        );

        var products = order.getItems().stream()
                .map(item -> new AddToCartRequest.Product(
                        item.getProduct().getName(),
                        "SKU-" + item.getProduct().getId(),
                        item.getQuantity(),
                        item.getProduct().getWeight() * item.getQuantity(),
                        item.getProduct().getWidth(),
                        item.getProduct().getHeight(),
                        item.getProduct().getLength(),
                        item.getSubtotal().doubleValue()
                )).toList();

        var volumes = order.getItems().stream()
                .map(item -> new AddToCartRequest.Volume(
                        item.getProduct().getWeight() * item.getQuantity(),
                        item.getProduct().getHeight(),
                        item.getProduct().getWidth(),
                        item.getProduct().getLength()
                )).toList();

        return new AddToCartRequest(
                option.id(), from, to, products, volumes,
                new AddToCartRequest.Options(false, false, String.valueOf(order.getId()))
        );
    }

    private ShippingStatus mapMeStatus(String meStatus) {
        if (meStatus == null) return null;
        return switch (meStatus.toLowerCase()) {
            case "released", "posted" -> ShippingStatus.POSTED;
            case "in_transit", "with_carrier" -> ShippingStatus.IN_TRANSIT;
            case "delivered" -> ShippingStatus.DELIVERED;
            case "canceled", "cancelled" -> ShippingStatus.CANCELLED;
            default -> null;
        };
    }
}