package com.arthuurdp.e_commerce.domain.dtos.address;

public record UpdateAddressRequest(String name, String street, Integer number, String complement, String neighborhood, Long cityId, Long stateId){
}
