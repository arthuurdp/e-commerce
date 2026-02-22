package com.arthuurdp.e_commerce.entities.dtos.address;

public record UpdateAddressRequest(String name, String street, Integer number, String complement, String neighborhood, Long cityId, Long stateId){
}
