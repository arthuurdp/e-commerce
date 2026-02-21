package com.arthuurdp.e_commerce.entities.dtos.address;

public record UpdateAddressRequest(String street, Integer number, String neighborhood, Long cityId){
}
