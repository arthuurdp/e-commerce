package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.address.StateResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.StateRepository;
import com.arthuurdp.e_commerce.services.mappers.AddressMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class StateService {
    private final StateRepository repo;
    private final AddressMapper mapper;

    public StateService(StateRepository repo, AddressMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public StateResponse findById(Long id) {
        return mapper.toStateResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("State not found")));
    }

    public Page<StateResponse> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(mapper::toStateResponse);
    }
}
