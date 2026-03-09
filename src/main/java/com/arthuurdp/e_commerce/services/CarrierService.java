package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.Carrier;
import com.arthuurdp.e_commerce.domain.entities.State;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CreateCarrierRequest;
import com.arthuurdp.e_commerce.domain.dtos.carrier.UpdateCarrierRequest;
import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CarrierRepository;
import com.arthuurdp.e_commerce.repositories.StateRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarrierService {
    private final CarrierRepository carrierRepository;
    private final StateRepository stateRepository;
    private final EntityMapperService entityMapperService;

    public CarrierService(CarrierRepository carrierRepository, StateRepository stateRepository, EntityMapperService entityMapperService) {
        this.carrierRepository = carrierRepository;
        this.stateRepository = stateRepository;
        this.entityMapperService = entityMapperService;
    }

    public CarrierResponse findById(Long id) {
        Carrier carrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        return entityMapperService.toCarrierResponse(carrier);
    }

    public Page<CarrierResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return carrierRepository.findAll(pageable).map(entityMapperService::toCarrierResponse);
    }

    public Page<CarrierResponse> findAllByStateIdAndStatus(int page, int size, Long stateId, CarrierStatus status) {
        if (!stateRepository.existsById(stateId)) {
            throw new ResourceNotFoundException("State not found");
        }
        Pageable pageable = PageRequest.of(page, size);
        return carrierRepository.findByStateIdAndStatus(stateId, status, pageable).map(entityMapperService::toCarrierResponse);
    }

    @Transactional
    public CarrierResponse create(CreateCarrierRequest req) {
        State state = stateRepository.findById(req.stateId()).orElseThrow(() -> new ResourceNotFoundException("State not found"));
        Carrier carrier = new Carrier(req.name(), state);
        return entityMapperService.toCarrierResponse(carrierRepository.save(carrier));
    }

    @Transactional
    public CarrierResponse update(Long id, UpdateCarrierRequest req) {
        Carrier existingCarrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        existingCarrier.setName(req.name());
        return entityMapperService.toCarrierResponse(carrierRepository.save(existingCarrier));
    }

    public void delete(Long id) {
        Carrier carrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        carrierRepository.deleteById(carrier.getId());
    }
}
