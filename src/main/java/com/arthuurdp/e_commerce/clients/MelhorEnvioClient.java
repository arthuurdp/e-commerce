package com.arthuurdp.e_commerce.clients;

import com.arthuurdp.e_commerce.domain.dtos.shipping.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around the Melhor Envio REST API v2.
 *
 * Sandbox base URL:   https://sandbox.melhorenvio.com.br/api/v2
 * Production base URL: https://melhorenvio.com.br/api/v2
 *
 * Required application.yml properties:
 *   melhorenvio.base-url=https://sandbox.melhorenvio.com.br/api/v2
 *   melhorenvio.token=<your bearer token>
 *   melhorenvio.app-email=<your app contact email>   (required by ME in User-Agent)
 *
 * Full label flow:
 *   1. calculate()     → pick cheapest / preferred option
 *   2. addToCart()     → returns meOrderId
 *   3. purchase()      → charges ME wallet
 *   4. generateLabel() → returns label PDF URL + tracking code
 */
@Component
public class MelhorEnvioClient {

    private static final Logger log = LoggerFactory.getLogger(MelhorEnvioClient.class);

    // Placeholder dimensions used until Product entity has real fields.
    // Unit: cm / kg
    private static final double DEFAULT_WEIGHT = 0.5;
    private static final int    DEFAULT_WIDTH   = 15;
    private static final int    DEFAULT_HEIGHT  = 10;
    private static final int    DEFAULT_LENGTH  = 20;

    private final WebClient webClient;

    @Value("${melhorenvio.from-postal-code}")
    private String fromPostalCode;

    public MelhorEnvioClient(
            @Value("${melhorenvio.base-url}") String baseUrl,
            @Value("${melhorenvio.token}")    String token,
            @Value("${melhorenvio.app-email}") String appEmail) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                // ME requires a User-Agent identifying your app
                .defaultHeader("User-Agent", "ecommerce-app (" + appEmail + ")")
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // 1. Calculate freight — returns list of available services
    // ─────────────────────────────────────────────────────────────

    public List<FreightOption> calculate(String toPostalCode, int itemCount) {
        var body = Map.of(
                "from",     Map.of("postal_code", fromPostalCode),
                "to",       Map.of("postal_code", toPostalCode),
                "package",  Map.of(
                        "weight", DEFAULT_WEIGHT * itemCount,
                        "width",  DEFAULT_WIDTH,
                        "height", DEFAULT_HEIGHT,
                        "length", DEFAULT_LENGTH
                ),
                "options",  Map.of("receipt", false, "own_hand", false),
                "services", "1,2"   // 1 = PAC, 2 = SEDEX (Correios via ME)
        );

        try {
            return webClient.post()
                    .uri("/me/shipment/calculate")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToFlux(FreightOption.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("ME calculate failed: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new MelhorEnvioApiException("Freight calculation failed", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Add label to cart — returns ME order ID
    // ─────────────────────────────────────────────────────────────

    public String addToCart(AddToCartRequest request) {
        try {
            var response = webClient.post()
                    .uri("/me/cart")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(CartResponse.class)
                    .block();

            if (response == null || response.id() == null) {
                throw new MelhorEnvioApiException("Empty response from ME cart endpoint");
            }
            return response.id();
        } catch (WebClientResponseException e) {
            log.error("ME addToCart failed: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new MelhorEnvioApiException("Failed to add shipment to ME cart", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 3. Purchase labels (checkout) — charges ME wallet
    // ─────────────────────────────────────────────────────────────

    public void purchase(List<String> meOrderIds) {
        var body = Map.of("orders", meOrderIds);
        try {
            webClient.post()
                    .uri("/me/shipment/checkout")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("ME purchase failed: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new MelhorEnvioApiException("Failed to purchase ME label", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Generate label — returns tracking code + label URL
    // ─────────────────────────────────────────────────────────────

    public LabelInfo generateLabel(String meOrderId) {
        var body = Map.of("orders", List.of(meOrderId));
        try {
            var response = webClient.post()
                    .uri("/me/shipment/generate")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GenerateLabelResponse.class)
                    .block();

            if (response == null) {
                throw new MelhorEnvioApiException("Empty response from ME generate endpoint");
            }
            return response.toLabelInfo(meOrderId);
        } catch (WebClientResponseException e) {
            log.error("ME generateLabel failed: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new MelhorEnvioApiException("Failed to generate ME label", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Internal response models
    // ─────────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FreightOption(
            Integer id,
            String name,
            @JsonProperty("custom_price") BigDecimal price,
            @JsonProperty("custom_delivery_time") Integer deliveryDays,
            String error
    ) {
        public boolean isAvailable() { return error == null || error.isBlank(); }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CartResponse(String id) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GenerateLabelResponse(Map<String, OrderLabelData> orders) {
        public LabelInfo toLabelInfo(String meOrderId) {
            if (orders == null || !orders.containsKey(meOrderId)) {
                throw new MelhorEnvioApiException("ME order not found in generate response: " + meOrderId);
            }
            var data = orders.get(meOrderId);
            return new LabelInfo(data.trackingCode(), data.labelUrl());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OrderLabelData(
            @JsonProperty("tracking_code") String trackingCode,
            @JsonProperty("label_url")     String labelUrl
    ) {}

    public record LabelInfo(String trackingCode, String labelUrl) {}

    // ─────────────────────────────────────────────────────────────
    // Cart request model (used by ShippingService)
    // ─────────────────────────────────────────────────────────────

    public record AddToCartRequest(
            @JsonProperty("service") Integer serviceId,
            @JsonProperty("from")    FromAddress from,
            @JsonProperty("to")      ToAddress to,
            @JsonProperty("products") java.util.List<Product> products,
            @JsonProperty("options") Options options
    ) {
        public record FromAddress(
                String name, String email, String document, String phone,
                @JsonProperty("postal_code") String postalCode,
                String address, String number, String district, String city,
                @JsonProperty("state_abbr") String state
        ) {}

        public record ToAddress(
                String name, String email, String document, String phone,
                @JsonProperty("postal_code") String postalCode,
                String address, String number, String district, String city,
                @JsonProperty("state_abbr") String state
        ) {}

        public record Product(
                String name, String quantity, int units,
                double weight, int width, int height, int length,
                @JsonProperty("unitary_value") double unitaryValue
        ) {}

        public record Options(boolean receipt, @JsonProperty("own_hand") boolean ownHand,
                              @JsonProperty("invoice_key") String invoiceKey) {}
    }

    public static class MelhorEnvioApiException extends RuntimeException {
        public MelhorEnvioApiException(String message) { super(message); }
        public MelhorEnvioApiException(String message, Throwable cause) { super(message, cause); }
    }
}