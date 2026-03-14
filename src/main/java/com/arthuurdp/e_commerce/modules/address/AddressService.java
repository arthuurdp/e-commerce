package com.arthuurdp.e_commerce.modules.address;

import com.arthuurdp.e_commerce.modules.address.dtos.CepLookupResponse;
import com.arthuurdp.e_commerce.modules.address.entity.Address;
import com.arthuurdp.e_commerce.modules.address.entity.City;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.address.dtos.AddressResponse;
import com.arthuurdp.e_commerce.modules.address.dtos.CreateAddressRequest;
import com.arthuurdp.e_commerce.modules.address.dtos.UpdateAddressRequest;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.address.mapper.AddressMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final CityService cityService;
    private final AddressMapper mapper;

    public AddressService(AddressRepository addressRepository, CityRepository cityRepository, CityService cityService, AddressMapper mapper) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
        this.cityService = cityService;
        this.mapper = mapper;
    }

    public AddressResponse findById(Long id, User user) {
        return addressRepository.findByIdAndUserId(id, user.getId()).map(mapper::toAddressResponse).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    public Page<AddressResponse> findAll(int page, int size, User user) {
        return addressRepository.findByUserId(PageRequest.of(page, size), user.getId()).map(mapper::toAddressResponse);
    }

    @Transactional
    public AddressResponse create(CreateAddressRequest req, User user) {
        CepLookupResponse lookup = cityService.lookupByCep(req.postalCode());

        City city = cityRepository.findById(lookup.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

        Address address = new Address(
                req.name(),
                req.street() != null ? req.street() : lookup.street(),
                req.number(),
                req.complement(),
                req.neighborhood() != null ? req.neighborhood() : lookup.neighborhood(),
                req.postalCode()
        );

        address.setCity(city);
        address.setUser(user);

        return mapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse update(Long id, UpdateAddressRequest req, User user) {
        Address address = addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Optional.ofNullable(req.name()).ifPresent(address::setName);
        Optional.ofNullable(req.number()).ifPresent(address::setNumber);
        Optional.ofNullable(req.complement()).ifPresent(address::setComplement);

        if (req.postalCode() != null) {
            CepLookupResponse lookup = cityService.lookupByCep(req.postalCode());
            City city = cityRepository.findById(lookup.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

            address.setPostalCode(req.postalCode());
            address.setCity(city);
            address.setStreet(Optional.ofNullable(req.street()).orElse(lookup.street()));
            address.setNeighborhood(Optional.ofNullable(req.neighborhood()).orElse(lookup.neighborhood()));
        } else {
            Optional.ofNullable(req.street()).ifPresent(address::setStreet);
            Optional.ofNullable(req.neighborhood()).ifPresent(address::setNeighborhood);
        }

        return mapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long id, User user) {
        addressRepository.delete(addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found")));
    }
}
