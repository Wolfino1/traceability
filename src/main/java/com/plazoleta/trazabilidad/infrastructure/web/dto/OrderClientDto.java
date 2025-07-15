package com.plazoleta.trazabilidad.infrastructure.web.dto;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;

public record OrderClientDto(
        Long orderId,
        Long clientId,
        OrderStatus status
) {}

