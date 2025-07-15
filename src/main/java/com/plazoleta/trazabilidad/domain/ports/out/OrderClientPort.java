package com.plazoleta.trazabilidad.domain.ports.out;

import com.plazoleta.trazabilidad.infrastructure.web.dto.OrderClientDto;

public interface OrderClientPort {
    OrderClientDto getOrderById(Long restaurantId, Long orderId, String authorizationHeader);
}

