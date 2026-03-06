package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Carrier;
import com.arthuurdp.e_commerce.entities.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CarrierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarrierService {
    private final CarrierRepository carrierRepository;
    private final EntityMapperService entityMapperService;

    public CarrierService(CarrierRepository carrierRepository, EntityMapperService entityMapperService) {
        this.carrierRepository = carrierRepository;
        this.entityMapperService = entityMapperService;
    }

    public CarrierResponse findById(Long id) {
        Carrier carrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        return entityMapperService.toCarrierResponse(carrier);
    }

    public Carrier findByShippingId(Long shippingId) {
        return carrierRepository.findByShippingId(shippingId).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
    }

    public Page<CarrierResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return carrierRepository.findAll(pageable).map(entityMapperService::toCarrierResponse);
    }

    public Carrier create(Carrier carrier) {
        return carrierRepository.save(carrier);
    }

    public Carrier update(Long id, Carrier carrier) {
        Carrier existingCarrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        existingCarrier.setName(carrier.getName());
        return carrierRepository.save(existingCarrier);
    }

    public void delete(Long id) {
        carrierRepository.deleteById(id);
    }
}
