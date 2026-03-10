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
import com.arthuurdp.e_commerce.services.mappers.AddressMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final AddressMapper mapper;

    public AddressService(AddressRepository addressRepository, CityRepository cityRepository, AddressMapper mapper) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
        this.mapper = mapper;
    }

    public AddressResponse findById(Long id, Long userId) {
        return addressRepository.findByIdAndUserId(id, userId).map(mapper::toAddressResponse).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    public Page<AddressResponse> findAll(int page, int size, Long userId) {
        return addressRepository.findByUserId(PageRequest.of(page, size), userId).map(mapper::toAddressResponse);
    }

    @Transactional
    public AddressResponse create(CreateAddressRequest req, User user) {
        City city = cityRepository.findById(req.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

        Address address = new Address(
                req.name(),
                req.street(),
                req.number(),
                req.complement(),
                req.neighborhood(),
                req.postalCode()
        );

        address.setCity(city);
        address.setUser(user);

        return mapper.toAddressResponse(addressRepository.save(address));
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
        if (req.cityId() != null && cityRepository.existsById(req.cityId())) {
            address.setCity(cityRepository.findById(req.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found")));
        }

        return mapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long id, User user) {
        addressRepository.delete(addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found")));
    }
}
