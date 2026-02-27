package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.dtos.CheckoutRequest;
import com.arthuurdp.e_commerce.entities.dtos.CheckoutResponse;
import com.arthuurdp.e_commerce.services.MercadoPagoService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final MercadoPagoService mercadoPagoService;

    public CheckoutController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/preference")
    public ResponseEntity<CheckoutResponse> createPreference(@RequestBody @Valid CheckoutRequest req) {
        try {
            CheckoutResponse response = mercadoPagoService.createPreference(req);
            return ResponseEntity.ok(response);
        } catch (MPException | MPApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoints de retorno (o MP redireciona o usuário para cá)
    @GetMapping("/success")
    public ResponseEntity<String> success(@RequestParam String payment_id,
                                          @RequestParam String status,
                                          @RequestParam String merchant_order_id) {
        // Aqui você atualiza o pedido no banco com base no payment_id
        return ResponseEntity.ok("Pagamento aprovado! ID: " + payment_id);
    }

    @GetMapping("/failure")
    public ResponseEntity<String> failure() {
        return ResponseEntity.ok("Pagamento falhou.");
    }

    @GetMapping("/pending")
    public ResponseEntity<String> pending() {
        return ResponseEntity.ok("Pagamento pendente.");
    }
}
