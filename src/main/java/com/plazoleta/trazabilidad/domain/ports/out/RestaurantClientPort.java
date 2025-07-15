package com.plazoleta.trazabilidad.domain.ports.out;

import com.plazoleta.trazabilidad.infrastructure.web.dto.RestaurantClientDto;

public interface RestaurantClientPort {
    RestaurantClientDto getById(Long restaurantId, String authHeader);

}
