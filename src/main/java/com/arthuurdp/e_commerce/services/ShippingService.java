package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.*;
import com.arthuurdp.e_commerce.domain.dtos.shipping.ShippingResponse;
import com.arthuurdp.e_commerce.domain.dtos.shipping.UpdateShippingRequest;
import com.arthuurdp.e_commerce.domain.enums.*;
import com.arthuurdp.e_commerce.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CarrierRepository;
import com.arthuurdp.e_commerce.repositories.OrderRepository;
import com.arthuurdp.e_commerce.repositories.ShippingCarrierRepository;
import com.arthuurdp.e_commerce.repositories.ShippingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShippingService {
    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;
    private final CarrierRepository carrierRepository;
    private final EntityMapperService entityMapperService;
    private final RouteService routeService;
    private final ShippingCarrierRepository shippingCarrierRepository;

    public ShippingService(ShippingRepository shippingRepository, OrderRepository orderRepository, CarrierRepository carrierRepository, EntityMapperService entityMapperService, RouteService routeService, ShippingCarrierRepository shippingCarrierRepository) {
        this.shippingRepository = shippingRepository;
        this.orderRepository = orderRepository;
        this.carrierRepository = carrierRepository;
        this.entityMapperService = entityMapperService;
        this.routeService = routeService;
        this.shippingCarrierRepository = shippingCarrierRepository;
    }

    @Transactional
    public ShippingResponse create(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getShipping() != null) {
            throw new ConflictException("Order already has a shipping");
        }

        Region originRegion = order.getOriginState().getRegion();
        Region destinationRegion = order.getAddress().getCity().getState().getRegion();

        List<Region> route = routeService.findRoute(originRegion, destinationRegion);

        Shipping shipping = new Shipping(order);
        shippingRepository.save(shipping);

        for (int i = 0; i < route.size(); i++) {
            Region region = route.get(i);

            Carrier carrier = carrierRepository
                    .findFirstByRegionAndStatus(region, CarrierStatus.AVAILABLE)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No available carrier for region: " + region
                    ));

            ShippingCarrier leg = new ShippingCarrier();
            leg.setShipping(shipping);
            leg.setCarrier(carrier);
            leg.setRegion(region);
            leg.setLegOrder(i + 1);

            if (i == 0) {
                leg.setStatus(ShippingCarrierStatus.IN_PROGRESS);
                carrier.setStatus(CarrierStatus.ON_DELIVERY);
                carrierRepository.save(carrier);
            } else {
                leg.setStatus(ShippingCarrierStatus.PENDING);
            }

            shippingCarrierRepository.save(leg);
        }

        return entityMapperService.toShippingResponse(shipping);
    }

    @Transactional
    public ShippingResponse handOff(Long shippingCarrierId) {
        ShippingCarrier leg = shippingCarrierRepository.findById(shippingCarrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Leg not found"));

        if (leg.getStatus() != ShippingCarrierStatus.IN_PROGRESS) {
            throw new BadRequestException("This leg is not in progress");
        }

        leg.setStatus(ShippingCarrierStatus.HANDED_OFF);
        leg.setHandedOffAt(LocalDateTime.now());
        leg.getCarrier().setStatus(CarrierStatus.AVAILABLE);
        carrierRepository.save(leg.getCarrier());
        shippingCarrierRepository.save(leg);

        Optional<ShippingCarrier> nextLeg = shippingCarrierRepository
                .findByShippingIdAndLegOrder(
                        leg.getShipping().getId(),
                        leg.getLegOrder() + 1
                );

        if (nextLeg.isPresent()) {
            nextLeg.get().setStatus(ShippingCarrierStatus.IN_PROGRESS);
            nextLeg.get().getCarrier().setStatus(CarrierStatus.ON_DELIVERY);
            carrierRepository.save(nextLeg.get().getCarrier());
            shippingCarrierRepository.save(nextLeg.get());
        } else {
            Shipping shipping = leg.getShipping();
            shipping.setStatus(ShippingStatus.DELIVERED);
            shipping.setDeliveredAt(LocalDateTime.now());
            shippingRepository.save(shipping);
        }

        return entityMapperService.toShippingResponse(leg.getShipping());
    }

    @Transactional
    public Page<ShippingResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return shippingRepository.findAll(pageable).map(entityMapperService::toShippingResponse);
    }

    public ShippingResponse findById(Long id) {
        return entityMapperService.toShippingResponse(shippingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shipping not found")));
    }
}