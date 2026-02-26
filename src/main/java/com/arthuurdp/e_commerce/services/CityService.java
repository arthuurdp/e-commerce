package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.dtos.address.CityResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private CityRepository cityRepository;
    private EntityMapperService entityMapperService;

    public CityService(CityRepository cityRepository, EntityMapperService entityMapperService) {
        this.cityRepository = cityRepository;
        this.entityMapperService = entityMapperService;
    }

    public Page<CityResponse> searchCities(Long stateId, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cityRepository.findByStateIdAndNameContainingIgnoreCase(pageable, stateId, query).map(entityMapperService::toCityResponse);
    }

    public CityResponse findById(Long id) {
        return cityRepository.findById(id).map(entityMapperService::toCityResponse).orElseThrow(() -> new ResourceNotFoundException("City not found"));
    }
}
