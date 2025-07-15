package com.plazoleta.trazabilidad.infrastructure.web.dto;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;

public record OrderClientDto(
                             Long restaurantId,
                             Long id,
                             Long clientId,
                             OrderStatus status,
                             Long employeeId) {
}
