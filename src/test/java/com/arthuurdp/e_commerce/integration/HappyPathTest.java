package com.arthuurdp.e_commerce.integration;

import com.arthuurdp.e_commerce.modules.auth.dtos.LoginRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.RegisterRequest;
import com.arthuurdp.e_commerce.modules.address.client.ViaCepClient;
import com.arthuurdp.e_commerce.modules.email.dtos.VerifyCodeRequest;
import com.arthuurdp.e_commerce.modules.shipping.client.MelhorEnvioClient;
import com.stripe.model.checkout.Session;
import com.arthuurdp.e_commerce.modules.user.enums.Gender;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Happy Path Test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class HappyPathTest extends BaseIntegrationTest {

    @Value("${stripe.webhook-secret}")
    private String stripeWebhookSecret;

    @Test
    @DisplayName("should register, verify email, login, add to cart, and calculate freight successfully")
    void shouldRegisterVerifyEmailLoginAddToCartAndCalculateFreightSuccessfully() throws Exception {

        // 0. Mock External APIs
        MelhorEnvioClient.FreightOption mockOption = new MelhorEnvioClient.FreightOption(
                1, "Sedex", BigDecimal.valueOf(25.50), 2, null
        );
        when(melhorEnvioClient.calculate(anyString(), anyInt())).thenReturn(List.of(mockOption));

        ViaCepClient.ViaCepResponse mockViaCep = new ViaCepClient.ViaCepResponse(
                "99300000", "Rua Teste", "Bairro Teste", "Teste City", "TS", false
        );
        when(viaCepClient.lookup(anyString())).thenReturn(mockViaCep);
        when(melhorEnvioClient.addToCart(any())).thenReturn("me_test_order_123");
        when(melhorEnvioClient.generateLabel(any())).thenReturn(new MelhorEnvioClient.LabelInfo("TRK123", "https://label.url"));

        Session mockStripeSession = mock(Session.class);
        when(mockStripeSession.getId()).thenReturn("cs_test_123");
        when(mockStripeSession.getUrl()).thenReturn("https://checkout.stripe.com/test");
        doReturn(mockStripeSession).when(paymentService).createStripeSession(any(), any(), any(), any());

        // 1. Register
        RegisterRequest registerReq = new RegisterRequest(
                "User", "Test", "usertest@gmail.com", "test123",
                "39209275080", "11987654321",
                LocalDate.of(1995, 6, 15), Gender.MALE
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("usertest@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("User"))
                .andExpect(jsonPath("$.id").isNumber());

        // 2. Login
        LoginRequest loginReq = new LoginRequest("usertest@gmail.com", "test123");
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginResponse).get("token").asText();

        // 3. Send Verification Email
        mockMvc.perform(post("/verify-email/send")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification email sent successfully!"));

        // 4. Get Code from Email
        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, atLeastOnce()).send(mailCaptor.capture());
        String emailBody = Objects.requireNonNull(mailCaptor.getValue().getText());
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(emailBody);
        String code = "";
        if (matcher.find()) {
            code = matcher.group();
        }
        Assertions.assertFalse(code.isEmpty(), "Verification code not found in email");

        // 5. Confirm Email
        mockMvc.perform(post("/verify-email/confirm")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VerifyCodeRequest(code))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully!"));

        // 6. List Products
        String productsResponse = mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Laptop"))
                .andReturn().getResponse().getContentAsString();

        Long productId = objectMapper.readTree(productsResponse).get("content").get(0).get("id").asLong();

        // 7. Add Product to Cart
        mockMvc.perform(patch("/cart/" + productId + "/increment")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.subtotal").value(2500.0));

        // 8. Calculate Freight
        mockMvc.perform(get("/cart/freight")
                        .param("postalCode", "01001000")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Sedex"))
                .andExpect(jsonPath("$[0].price").value(25.50));

        // 9. Create Address
        String addressReq = """
                {
                    "name": "Casa",
                    "street": "Av 9 de Julho",
                    "number": 1337,
                    "complement": "Apartamento",
                    "neighborhood": "Centro",
                    "postalCode": "99300000"
                }
                """;

        String addressResponse = mockMvc.perform(post("/addresses")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(addressReq))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Casa"))
                .andExpect(jsonPath("$.street").value("Av 9 de Julho"))
                .andExpect(jsonPath("$.number").value(1337))
                .andExpect(jsonPath("$.complement").value("Apartamento"))
                .andExpect(jsonPath("$.neighborhood").value("Centro"))
                .andReturn().getResponse().getContentAsString();

        Long addressId = objectMapper.readTree(addressResponse).get("id").asLong();

        // 10. Create Order
        String orderReq = """
        {
          "addressId": %d,
          "paymentMethod": "CREDIT_CARD"
        }
        """.formatted(addressId);

        String orderCheckout = mockMvc.perform(post("/orders/checkout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderReq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").isNumber())
                .andExpect(jsonPath("$.sessionId").value("cs_test_123"))
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.stripe.com/test"))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderCheckout).get("orderId").asLong();

        // 11. Simulate Stripe Webhook
        String payload = """
                {
                  "id": "evt_test_123",
                  "object": "event",
                  "type": "checkout.session.completed",
                  "data": {
                    "object": {
                      "id": "cs_test_123",
                      "object": "checkout.session",
                      "metadata": {
                        "orderId": "%d"
                      },
                      "payment_status": "paid",
                      "status": "complete"
                    }
                  }
                }
                """.formatted(orderId);

        long timestamp = Instant.now().getEpochSecond();
        String signedPayload = timestamp + "." + payload;
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(stripeWebhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String signature = hexString.toString();
        String sigHeader = "t=" + timestamp + ",v1=" + signature;

        mockMvc.perform(post("/webhook/stripe")
                        .header("Stripe-Signature", sigHeader)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 12. Verify Order Status
        mockMvc.perform(get("/orders/" + orderId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        // 13. Verify Shipping Status
        mockMvc.perform(get("/orders/" + orderId + "/shipping")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LABEL_GENERATED"));
    }
}