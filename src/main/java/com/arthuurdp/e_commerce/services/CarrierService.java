package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.Carrier;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CreateCarrierRequest;
import com.arthuurdp.e_commerce.domain.dtos.carrier.UpdateCarrierRequest;
import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.domain.enums.Region;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CarrierRepository;
import com.arthuurdp.e_commerce.services.mappers.CarrierMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarrierService {
    private final CarrierRepository repo;
    private final CarrierMapper mapper;

    public CarrierService(CarrierRepository repo, CarrierMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public CarrierResponse findById(Long id) {
        return mapper.toCarrierResponse(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found")));
    }

    public Page<CarrierResponse> findAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(mapper::toCarrierResponse);
    }

    public Page<CarrierResponse> findAllByRegion(int page, int size, Region region, CarrierStatus status) {
        if (status != null) {
            return repo.findByRegionAndStatus(region, status, PageRequest.of(page, size)).map(mapper::toCarrierResponse);
        }
        return repo.findByRegion(region, PageRequest.of(page, size)).map(mapper::toCarrierResponse);
    }

    @Transactional
    public CarrierResponse create(CreateCarrierRequest req) {
        if (repo.existsByEmail(req.email())) {
            throw new ConflictException("Carrier already exists");
        }

        Carrier carrier = new Carrier(
                req.name(),
                req.cnpj(),
                req.email(),
                req.phone(),
                req.region()
        );

        return mapper.toCarrierResponse(repo.save(carrier));
    }

    @Transactional
    public CarrierResponse update(Long id, UpdateCarrierRequest req) {
        Carrier carrier = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));

        if (req.name() != null) {
            carrier.setName(req.name());
        }

        if (req.email() != null) {
            if (repo.existsByEmail(req.email()) && !carrier.getEmail().equals(req.email())) {
                throw new ConflictException("Email already in use by another carrier");
            }
            carrier.setEmail(req.email());
        }

        if (req.phone() != null) {
            carrier.setPhone(req.phone());
        }

        if (req.region() != null) {
            carrier.setRegion(req.region());
        }

        if (req.status() != null) {
            carrier.setStatus(req.status());
        }

        return mapper.toCarrierResponse(repo.save(carrier));
    }

    public void delete(Long id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found")));
    }
}
