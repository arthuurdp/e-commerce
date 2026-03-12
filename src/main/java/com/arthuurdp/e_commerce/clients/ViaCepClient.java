package com.arthuurdp.e_commerce.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ViaCepClient {
    private final WebClient webClient;

    public ViaCepClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://viacep.com.br/ws")
                .build();
    }

    public ViaCepResponse lookup(String cep) {
        String cleanCep = cep.replaceAll("\\D", "");
        return webClient.get()
                .uri("/{cep}/json/", cleanCep)
                .retrieve()
                .bodyToMono(ViaCepResponse.class)
                .block();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ViaCepResponse(
            String cep,
            String logradouro,
            String bairro,
            @JsonProperty("localidade") String city,
            @JsonProperty("uf") String stateUf,
            boolean erro
    ) {}
}