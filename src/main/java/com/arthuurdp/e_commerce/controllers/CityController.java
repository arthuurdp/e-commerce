package com.arthuurdp.e_commerce.controllers;

import com.arthuurdp.e_commerce.entities.State;
import com.arthuurdp.e_commerce.entities.dtos.address.CityResponse;
import com.arthuurdp.e_commerce.services.CityService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService service;

    public CityController(CityService service) {
        this.service = service;
    }

    @GetMapping
    public Page<CityResponse> searchCities(
            @RequestParam Long stateId,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){
        return service.searchCities(stateId, query, page, size);
    }

    @GetMapping("/{id}")
    public CityResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
