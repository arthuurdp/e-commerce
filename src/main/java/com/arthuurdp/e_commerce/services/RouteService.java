package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.enums.Region;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteService {
    public List<Region> findRoute(Region origin, Region destination) {
        List<Region> order = List.of(
                Region.NORTH,
                Region.NORTHEAST,
                Region.MIDWEST,
                Region.SOUTHEAST,
                Region.SOUTH
        );

        int originIndex = order.indexOf(origin);
        int destinationIndex = order.indexOf(destination);

        if (originIndex <= destinationIndex) {
            return order.subList(originIndex, destinationIndex + 1);
        } else {
            List<Region> reversed = new ArrayList<>(order.subList(destinationIndex, originIndex + 1));
            Collections.reverse(reversed);
            return reversed;
        }
    }
}
