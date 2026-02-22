package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.dtos.address.CityResponse;
import com.arthuurdp.e_commerce.entities.dtos.address.StateResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.StateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StateService {
    private final StateRepository repo;
    private final EntityMapperService entityMapperService;

    public StateService(StateRepository repo, EntityMapperService entityMapperService) {
        this.repo = repo;
        this.entityMapperService = entityMapperService;
    }

    public StateResponse findById(Long id) {
        return entityMapperService.toStateResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("State not found")));
    }

    public Page<StateResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable).map(entityMapperService::toStateResponse);
    }
}
