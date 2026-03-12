package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.domain.dtos.address.CepLookupResponse;
import com.arthuurdp.e_commerce.domain.dtos.address.CityResponse;
import com.arthuurdp.e_commerce.services.CityService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService service;

    public CityController(CityService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<CityResponse>> searchCities(
            @RequestParam Long stateId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok().body(service.searchCities(stateId, query, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @GetMapping("/lookup")
    public ResponseEntity<CepLookupResponse> lookupByCep(
            @RequestParam String cep
    ) {
        return ResponseEntity.ok().body(service.lookupByCep(cep));
    }
}
