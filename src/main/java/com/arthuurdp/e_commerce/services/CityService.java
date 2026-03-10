package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.places.CityResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CityRepository;
import com.arthuurdp.e_commerce.services.mappers.AddressMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityRepository repo;
    private final AddressMapper mapper;

    public CityService(CityRepository repo, AddressMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public Page<CityResponse> searchCities(Long stateId, String query, int page, int size) {
        return repo.findByStateIdAndNameContainingIgnoreCase(PageRequest.of(page, size), stateId, query).map(mapper::toCityResponse);
    }

    public CityResponse findById(Long id) {
        return repo.findById(id).map(mapper::toCityResponse).orElseThrow(() -> new ResourceNotFoundException("City not found"));
    }
}
