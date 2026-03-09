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

    // ME tracking URL pattern
    private static final String TRACKING_URL = "https://melhorrastreio.com.br/rastreio/";

    private final ShippingRepository  shippingRepository;
    private final MelhorEnvioClient   melhorEnvioClient;
    private final EntityMapperService mapper;

    @Value("${melhorenvio.from-postal-code}")
    private String fromPostalCode;

    public ShippingService(ShippingRepository shippingRepository,
                           MelhorEnvioClient melhorEnvioClient,
                           EntityMapperService mapper) {
        this.shippingRepository = shippingRepository;
        this.melhorEnvioClient  = melhorEnvioClient;
        this.mapper             = mapper;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Called by WebhookService after Stripe confirms payment
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void createForOrder(Order order) {
        if (shippingRepository.existsByOrderId(order.getId())) {
            log.warn("Shipping already exists for order {}, skipping", order.getId());
            return;
        }

        // Persist a PENDING record immediately so the order has a shipping entry
        // even if the ME API calls below fail
        Shipping shipping = shippingRepository.save(new Shipping(order));

        try {
            // Destination postal code comes from the order's shipping address
            String toPostalCode = order.getAddress().getPostalCode().replaceAll("\\D", "");
            int    itemCount    = order.getItems().size();

            // 1. Calculate freight options and pick the cheapest available one
            List<FreightOption> options = melhorEnvioClient.calculate(toPostalCode, itemCount);
            FreightOption chosen = options.stream()
                    .filter(FreightOption::isAvailable)
                    .min(Comparator.comparing(FreightOption::price))
                    .orElseThrow(() -> new MelhorEnvioApiException("No freight options available for CEP " + toPostalCode));

            log.info("Order {}: chose freight '{}' at R$ {} ({} days)",
                    order.getId(), chosen.name(), chosen.price(), chosen.deliveryDays());

            // 2. Add to ME cart
            var cartRequest = buildCartRequest(order, chosen, toPostalCode);
            String meOrderId = melhorEnvioClient.addToCart(cartRequest);

            // 3. Purchase (deduct from ME wallet)
            melhorEnvioClient.purchase(List.of(meOrderId));

            // 4. Generate label → get tracking code + label URL
            var labelInfo = melhorEnvioClient.generateLabel(meOrderId);

            // Persist all ME data
            shipping.setMeOrderId(meOrderId);
            shipping.setCarrier(chosen.name());
            shipping.setShippingCost(chosen.price());
            shipping.setTrackingCode(labelInfo.trackingCode());
            shipping.setTrackingUrl(TRACKING_URL + labelInfo.trackingCode());
            shipping.setLabelUrl(labelInfo.labelUrl());
            shipping.setStatus(ShippingStatus.LABEL_PURCHASED);

            shippingRepository.save(shipping);
            log.info("Order {}: label purchased — tracking {}", order.getId(), labelInfo.trackingCode());

        } catch (Exception e) {
            // Don't lose the shipping row — mark as FAILED so admins can retry
            shipping.setStatus(ShippingStatus.FAILED);
            shippingRepository.save(shipping);
            log.error("Order {}: ME label creation failed — {}", order.getId(), e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Called by MelhorEnvioWebhookController when ME sends a tracking event
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void handleWebhookEvent(MelhorEnvioWebhookEvent event) {
        Shipping shipping = shippingRepository.findByMeOrderId(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No shipping found for ME order: " + event.orderId()));

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

    // ─────────────────────────────────────────────────────────────────────────
    // GET for users
    // ─────────────────────────────────────────────────────────────────────────

    public ShippingResponse getShippingForUser(Long orderId, Long userId) {
        Shipping shipping = shippingRepository.findByOrderIdAndOrderUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shipping not found for order " + orderId));
        return mapper.toShippingResponse(shipping);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private AddToCartRequest buildCartRequest(Order order, FreightOption option, String toPostalCode) {
        Address addr     = order.getAddress();
        String  cityName = addr.getCity().getName();
        String  stateUf  = addr.getCity().getState().getUf();
        String  numStr   = String.valueOf(addr.getNumber());
        String  name     = order.getUser().getFirstName() + " " + order.getUser().getLastName();
        String  email    = order.getUser().getEmail();
        String  doc      = order.getUser().getCpf();
        String  phone    = order.getUser().getPhone();

        // "From" is your store — using the same address as placeholder until you
        // add a dedicated store-address config. Replace fromPostalCode field is already
        // injected in MelhorEnvioClient; here we reuse toPostalCode as a safe fallback.
        var from = new AddToCartRequest.FromAddress(
                name, email, doc, phone,
                fromPostalCode,
                addr.getStreet(), numStr,
                addr.getNeighborhood(), cityName, stateUf
        );

        var to = new AddToCartRequest.ToAddress(
                name, email, doc, phone,
                toPostalCode,
                addr.getStreet(), numStr,
                addr.getNeighborhood(), cityName, stateUf
        );

        var products = List.of(new AddToCartRequest.Product(
                "Pedido #" + order.getId(),
                "SKU-" + order.getId(),
                order.getItems().size(),
                0.5 * order.getItems().size(),  // placeholder weight
                15, 10, 20,                     // placeholder dimensions
                order.getTotal().doubleValue()
        ));

        return new AddToCartRequest(
                option.id(), from, to, products,
                new AddToCartRequest.Options(false, false, String.valueOf(order.getId()))
        );
    }

    /**
     * Maps Melhor Envio webhook status strings to our internal ShippingStatus.
     * ME sends statuses like "posted", "delivered", "canceled", etc.
     * Returns null for statuses we intentionally ignore.
     */
    private ShippingStatus mapMeStatus(String meStatus) {
        if (meStatus == null) return null;
        return switch (meStatus.toLowerCase()) {
            case "posted"                       -> ShippingStatus.POSTED;
            case "in_transit", "with_carrier"   -> ShippingStatus.IN_TRANSIT;
            case "delivered"                    -> ShippingStatus.DELIVERED;
            case "canceled", "cancelled"        -> ShippingStatus.CANCELLED;
            default                             -> null;
        };
    }
}