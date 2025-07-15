package com.plazoleta.trazabilidad.domain.ports.out;

import com.plazoleta.trazabilidad.infrastructure.web.dto.OrderClientDto;
import com.plazoleta.trazabilidad.infrastructure.web.dto.OrderSummaryDto;

import java.util.List;

public interface OrderClientPort {
    OrderClientDto getOrderById(Long restaurantId, Long orderId, String authorizationHeader);
    List<OrderSummaryDto> getOrdersByRestaurant(String authHeader, Long restaurantId);

}

