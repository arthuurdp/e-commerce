package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.Address;
import com.arthuurdp.e_commerce.domain.entities.City;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.domain.dtos.address.CreateAddressRequest;
import com.arthuurdp.e_commerce.domain.dtos.address.UpdateAddressRequest;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.AddressRepository;
import com.arthuurdp.e_commerce.repositories.CityRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final EntityMapperService entityMapperService;

    public AddressService(AddressRepository addressRepository, CityRepository cityRepository, EntityMapperService entityMapperService) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
        this.entityMapperService = entityMapperService;
    }

    public AddressResponse findById(Long id, Long userId) {
        return addressRepository.findByIdAndUserId(id, userId).map(entityMapperService::toAddressResponse).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    public Page<AddressResponse> findAll(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        return addressRepository.findByUserId(pageable, userId).map(entityMapperService::toAddressResponse);
    }

    @Transactional
    public AddressResponse create(CreateAddressRequest req, User user) {
        City city = cityRepository.findById(req.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

        Address address = new Address(
                req.name(),
                req.street(),
                req.number(),
                req.complement(),
                req.neighborhood()
        );

        address.setCity(city);
        address.setUser(user);

        return entityMapperService.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse update(Long id, UpdateAddressRequest req, User user) {
        Address address = addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (req.name() != null) {
            address.setName(req.name());
        }
        if (req.street() != null) {
            address.setStreet(req.street());
        }
        if (req.number() != null) {
            address.setNumber(req.number());
        }
        if (req.complement() != null) {
            address.setComplement(req.complement());
        }
        if (req.neighborhood() != null) {
            address.setNeighborhood(req.neighborhood());
        }
        if (req.cityId() != null) {
            if (cityRepository.existsById(req.cityId())) {
                address.setCity(cityRepository.findById(req.cityId()).orElseThrow());
            }
        }

        return entityMapperService.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long id, User user) {
        Address address = addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }
}
