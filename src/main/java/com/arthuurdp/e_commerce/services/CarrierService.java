package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Carrier;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CarrierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CarrierService {
    private final CarrierRepository carrierRepository;

    public CarrierService(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

    public Carrier findById(Long id) {
        return carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
    }

    public Carrier findByShippingId(Long shippingId) {
        return carrierRepository.findByShippingId(shippingId).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
    }

    public Page<Carrier> findAll(int page, int size) {
        return carrierRepository.findAll(PageRequest.of(page, size));
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
